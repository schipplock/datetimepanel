/*
 * Copyright 2023 Andreas Schipplock
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schipplock.gui.swing.datetimepanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.Serial;
import java.time.LocalDateTime;

import static java.lang.String.format;

public class DateTimePanel extends JPanel implements Panel {

    @Serial
    private static final long serialVersionUID = -2253245667639591999L;

    private final DatePanel datePanel;

    private final TimePanel timePanel;

    public DateTimePanel(LocalDateTime localDateTime) {
        super(new MigLayout(getLayoutConstraints()), true);

        var datePanelConstraints = m("panel.date.constraints");
        var timePanelConstraints = m("panel.time.constraints");

        datePanel = new DatePanel(localDateTime.toLocalDate());
        timePanel = new TimePanel(localDateTime.toLocalTime());

        add(datePanel, datePanelConstraints);
        add(new JLabel(" "));
        add(timePanel, timePanelConstraints);
    }

    private static String getLayoutConstraints() {
        var constraints = System.getenv("MIGLAYOUT_CONSTRAINTS");
        var layoutDefaults = "gap 0 0, ins 0";

        if (constraints == null) {
            constraints = layoutDefaults;
        } else {
            constraints = format("%s, %s", layoutDefaults, constraints);
        }

        return constraints;
    }

    public DateTimePanel() {
        this(LocalDateTime.now());
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.of(datePanel.getLocalDate(), timePanel.getLocalTime());
    }

    public void onChange(Runnable changeNotifier) {
        datePanel.onChange(changeNotifier);
        timePanel.onChange(changeNotifier);
    }

    public boolean isValidDateTime() {
        try {
            getLocalDateTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
