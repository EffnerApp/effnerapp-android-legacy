/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 17.09.21, 21:19.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.data.api.json.data;

public class DataResponse {
    private String motd;
    private Timetable[] timetables;
    private Exams exams;
    private Document[] documents;

    public String getMotd() {
        return motd;
    }

    public Exams getExams() {
        return exams;
    }

    public Timetable[] getTimetables() {
        return timetables;
    }

    public Document[] getDocuments() {
        return documents;
    }

    public Document getDocumentByKey(String key) {
        for (Document document : documents) {
            if (document.getKey().equals(key)) {
                return document;
            }
        }
        return null;
    }
}