package com.iceberg.watchrrev1;

public class MCSData {
    private int id;
    private String title;
    private String release_date;
    private String posters;
    private int runtimes;
    private boolean watchlist;

    public MCSData(int id, String title, String release_date, String posters, int runtimes,boolean onWatchlist) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.posters = posters;
        this.runtimes = runtimes;
        this.watchlist = onWatchlist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPosters() {
        return posters;
    }

    public void setPosters(String posters) {
        this.posters = posters;
    }

    public int getRuntimes() {
        return runtimes;
    }

    public void setRuntimes(int runtimes) {
        this.runtimes = runtimes;
    }

    public boolean getWatchlist() {return watchlist;}

    public void setWatchlist(boolean watchlist) { this.watchlist = watchlist; }
}
