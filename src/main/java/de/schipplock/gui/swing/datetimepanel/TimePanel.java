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

import de.schipplock.gui.swing.datetimepanel.exceptions.InvalidTimeValuesException;
import de.schipplock.gui.swing.datetimepanel.verifier.Verifier;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class TimePanel extends JPanel implements Panel {

    @Serial
    private static final long serialVersionUID = 7115382014751309738L;

    private final boolean is12hourMode = Boolean.parseBoolean(m("12hour"));

    private final DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder().appendPattern("H:mm");

    private final DateTimeFormatterBuilder dateTimeFormatterBuilder12Hours = new DateTimeFormatterBuilder().appendPattern("h:mm ").appendText(ChronoField.AMPM_OF_DAY);

    private final Set<JTextField> invalidTextFields = new HashSet<>();

    private final JTextField hourTextField = new JTextField(2);

    private final JTextField minuteTextField = new JTextField(2);

    private final JComboBox<String> ampmComboBox = new JComboBox<>(new String[] {"AM", "PM"});

    private final Verifier hourVerifier = (Verifier & Serializable) value -> {
        try {
            var hourLimit = is12hourMode ? 12 : 23;
            return Integer.parseInt(value) >= 1 && Integer.parseInt(value) <= hourLimit;
        } catch (NumberFormatException ex) {
            return false;
        }
    };

    private final Verifier minuteVerifier = (Verifier & Serializable) value -> {
        try {
            return Integer.parseInt(value) >= 1 && Integer.parseInt(value) <= 59;
        } catch (NumberFormatException ex) {
            return false;
        }
    };

    public TimePanel(LocalTime localTime) {
        super(new MigLayout(Panel.getLayoutConstraints()), true);

        setBorder(new PanelBorder());

        var hourLabel = new JLabel(m("hour.caption"));
        hourTextField.setToolTipText(m("hour.tooltip"));
        hourTextField.setHorizontalAlignment(SwingConstants.CENTER);

        var minuteLabel = new JLabel(m("minute.caption"));
        minuteTextField.setToolTipText(m("minute.tooltip"));
        minuteTextField.setHorizontalAlignment(SwingConstants.CENTER);

        hourTextField.addKeyListener(createTextFieldKeyListener(invalidTextFields, hourVerifier));
        minuteTextField.addKeyListener(createTextFieldKeyListener(invalidTextFields, minuteVerifier));

        ampmComboBox.setMinimumSize(new Dimension(20, ampmComboBox.getHeight()));

        add(hourLabel, format("%s, span 2", m("panel.time.field.hour.constraints")));
        add(minuteLabel, format("%s, span 2, wrap", m("panel.time.field.minute.constraints")));

        add(hourTextField, m("panel.time.field.hour.constraints"));
        add(new JLabel(" : "), "");
        add(minuteTextField, m("panel.time.field.minute.constraints"));

        hourTextField.setText(localTime.format(new DateTimeFormatterBuilder().appendPattern("H").toFormatter()));
        var minute = localTime.format(new DateTimeFormatterBuilder().appendPattern("mm").toFormatter());
        minuteTextField.setText(minute);

        if (is12hourMode) {
            add(new JLabel(" "), "");
            hourTextField.setText(localTime.format(new DateTimeFormatterBuilder().appendPattern("h").toFormatter()));
            var ampmValue = localTime.format(new DateTimeFormatterBuilder().appendPattern("").appendText(ChronoField.AMPM_OF_DAY).toFormatter());
            ampmComboBox.getModel().setSelectedItem(ampmValue);
            add(ampmComboBox, m("panel.time.field.ampm.constraints"));
        }
    }

    public TimePanel() {
        this(LocalTime.now());
    }

    public LocalTime getLocalTime() {
        if (!invalidTextFields.isEmpty()) {
            throw new InvalidTimeValuesException(format("could not create LocalTime object for given time values, hour: %s, minute: %s",
                    hourTextField.getText(), minuteTextField.getText()));
        }
        var ampmValue = ampmComboBox.getModel().getElementAt(ampmComboBox.getSelectedIndex());
        if (is12hourMode) {
            var parsableDateTimeString = format("%s:%s %s", hourTextField.getText(), minuteTextField.getText(), ampmValue);
            return LocalTime.parse(parsableDateTimeString, dateTimeFormatterBuilder12Hours.toFormatter());
        }
        var parsableDateTimeString = format("%s:%s", hourTextField.getText(), minuteTextField.getText());
        return LocalTime.parse(parsableDateTimeString, dateTimeFormatterBuilder.toFormatter());
    }

    public void onChange(Runnable changeNotifier) {
        hourTextField.addKeyListener(createChangeNotifierKeyListener(changeNotifier));
        minuteTextField.addKeyListener(createChangeNotifierKeyListener(changeNotifier));
    }

    public boolean isValidTime() {
        try {
            getLocalTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
