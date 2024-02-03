package io.projectBot.TestBot.service;

import java.time.LocalTime;

public class Notification {
        private final LocalTime eventTime ;
        private final String message;

        public Notification(LocalTime eventTime, String message) {
            this.eventTime = eventTime;
            this.message = message;
        }

        public LocalTime getEventTime() {
            return eventTime;
        }

        public String getMessage() {
            return message;
        }



}
