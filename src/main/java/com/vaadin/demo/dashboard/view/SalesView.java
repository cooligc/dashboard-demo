package com.vaadin.demo.dashboard.view;

import java.awt.Color;
import java.util.Collection;
import java.util.Date;

import com.vaadin.addon.timeline.Timeline;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.demo.dashboard.DashboardUI;
import com.vaadin.demo.dashboard.data.dummy.DummyDataProvider;
import com.vaadin.demo.dashboard.domain.Movie;
import com.vaadin.demo.dashboard.domain.MovieRevenue;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class SalesView extends VerticalLayout implements View {

    private final Timeline timeline;

    Color[] colors = new Color[] { new Color(52, 154, 255),
            new Color(242, 81, 57), new Color(255, 201, 35),
            new Color(83, 220, 164) };
    int colorIndex = -1;

    public SalesView() {
        setSizeFull();
        addStyleName("timeline");

        Label header = new Label("Revenue by Movie Title");
        header.addStyleName("h1");
        addComponent(header);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setSpacing(true);
        toolbar.setMargin(true);
        toolbar.addStyleName("toolbar");
        addComponent(toolbar);

        Collection<Movie> movies = DummyDataProvider.getMovies();
        final ComboBox movieSelect = new ComboBox(null, movies);
        movieSelect.setWidth("300px");
        movieSelect.setItemCaptionPropertyId("title");
        toolbar.addComponent(movieSelect);
        movieSelect.addShortcutListener(new ShortcutListener("Add",
                KeyCode.ENTER, null) {

            @Override
            public void handleAction(Object sender, Object target) {
                addSelectedMovie(movieSelect);
            }
        });

        Button add = new Button("Add");
        add.addStyleName("default");
        add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                addSelectedMovie(movieSelect);
            }
        });
        toolbar.addComponent(add);
        toolbar.setComponentAlignment(add, Alignment.BOTTOM_LEFT);

        Button clear = new Button("Clear");
        clear.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                timeline.removeAllGraphDataSources();
            }
        });
        toolbar.addComponent(clear);
        toolbar.setComponentAlignment(clear, Alignment.BOTTOM_RIGHT);
        toolbar.setExpandRatio(clear, 1);

        timeline = new Timeline();
        timeline.setDateSelectVisible(false);
        timeline.setChartModesVisible(false);
        timeline.setGraphShadowsEnabled(false);
        timeline.setZoomLevelsVisible(false);
        timeline.setSizeFull();
        timeline.setNoDataSourceCaption("<span class=\"v-label h2 light\">Add a data set from the dropdown above</span>");

        addComponent(timeline);
        setExpandRatio(timeline, 1);

        // Add first 4 by default
        int i = 0;
        for (Movie m : DummyDataProvider.getMovies()) {
            addDataSet(m);
            if (++i > 3) {
                break;
            }
        }

        Date start = new Date();
        start.setMonth(start.getMonth() - 2);
        Date end = new Date();
        if (timeline.getGraphDatasources().size() > 0) {
            timeline.setVisibleDateRange(start, end);
        }

    }

    private void addSelectedMovie(final ComboBox movieSelect) {
        if (movieSelect.getValue() != null) {
            Movie movie = (Movie) movieSelect.getValue();
            addDataSet(movie);
            movieSelect.removeItem(movie);
            movieSelect.setValue(null);
        }
    }

    private void addDataSet(Movie movie) {

        Collection<MovieRevenue> transactionsForMovie = DashboardUI
                .getDataProvider().getRevenueByMovie(movie.getId());

        BeanItemContainer<MovieRevenue> revenue = new BeanItemContainer<MovieRevenue>(
                MovieRevenue.class, transactionsForMovie);

        revenue.sort(new Object[] { "timestamp" }, new boolean[] { true });

        timeline.addGraphDataSource(revenue, "timestamp", "revenue");
        colorIndex = (colorIndex >= colors.length - 1 ? 0 : ++colorIndex);
        timeline.setGraphOutlineColor(revenue, colors[colorIndex]);
        timeline.setBrowserOutlineColor(revenue, colors[colorIndex]);
        timeline.setBrowserFillColor(revenue, colors[colorIndex].brighter());
        timeline.setGraphLegend(revenue, movie.getTitle());
        timeline.setEventCaptionPropertyId("date");
        timeline.setVerticalAxisLegendUnit(revenue, "$");
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub
    }
}
