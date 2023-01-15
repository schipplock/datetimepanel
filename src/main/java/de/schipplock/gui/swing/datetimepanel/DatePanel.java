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

import de.schipplock.gui.swing.datetimepanel.exceptions.InvalidDateValuesException;
import de.schipplock.gui.swing.datetimepanel.exceptions.UnexpectedFieldException;
import de.schipplock.gui.swing.datetimepanel.verifier.Verifier;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static java.lang.String.format;

public class DatePanel extends JPanel implements Panel {

    @Serial
    private static final long serialVersionUID = 7313019012586822265L;

    private final Set<JTextField> invalidTextFields = new HashSet<>();

    private final JTextField yearTextField = new JTextField(4);

    private final JTextField monthTextField = new JTextField(2);

    private final JTextField dayTextField = new JTextField(2);

    private final Verifier dayVerifier = (Verifier & Serializable) value -> {
        try {
            return Integer.parseInt(value) >= 1 && Integer.parseInt(value) <= 31;
        } catch (NumberFormatException ex) {
            return false;
        }
    };

    private final Verifier monthVerifier = (Verifier & Serializable) value -> {
        try {
            return Integer.parseInt(value) >= 1 && Integer.parseInt(value) <= 12;
        } catch (NumberFormatException ex) {
            return false;
        }
    };

    private final Verifier yearVerifier = (Verifier & Serializable) value -> {
        try {
            return Integer.parseInt(value) >= 2000;
        } catch (NumberFormatException ex) {
            return false;
        }
    };

    public DatePanel(LocalDate localDate) {
        super(new MigLayout(Panel.getLayoutConstraints()), true);

        setBorder(new PanelBorder());

        var dayLabel = new JLabel(m("day.caption"));
        dayTextField.setText(String.valueOf(localDate.getDayOfMonth()));
        dayTextField.setToolTipText(m("day.tooltip"));
        dayTextField.setHorizontalAlignment(SwingConstants.CENTER);

        var monthLabel = new JLabel(m("month.caption"));
        monthTextField.setText(String.valueOf(localDate.getMonthValue()));
        monthTextField.setToolTipText(m("month.tooltip"));
        monthTextField.setHorizontalAlignment(SwingConstants.CENTER);

        var yearLabel = new JLabel(m("year.caption"));
        yearTextField.setText(String.valueOf(localDate.getYear()));
        yearTextField.setToolTipText(m("year.tooltip"));
        yearTextField.setHorizontalAlignment(SwingConstants.CENTER);

        dayTextField.addKeyListener(createTextFieldKeyListener(invalidTextFields, dayVerifier));
        monthTextField.addKeyListener(createTextFieldKeyListener(invalidTextFields, monthVerifier));
        yearTextField.addKeyListener(createTextFieldKeyListener(invalidTextFields, yearVerifier));

        Queue<JTextField> dateFieldQueue = new LinkedList<>();
        Queue<JLabel> dateLabelQueue = new LinkedList<>();

        var fields = m("date.ui.fields").split("");

        for (var field : fields) {
            switch (field) {
                case "d" -> {
                    dayLabel.putClientProperty("constraints", m("panel.date.field.day.constraints"));
                    dayTextField.putClientProperty("constraints", m("panel.date.field.day.constraints"));
                    dateLabelQueue.add(dayLabel);
                    dateFieldQueue.add(dayTextField);
                }
                case "m" -> {
                    monthLabel.putClientProperty("constraints", m("panel.date.field.month.constraints"));
                    monthTextField.putClientProperty("constraints", m("panel.date.field.month.constraints"));
                    dateLabelQueue.add(monthLabel);
                    dateFieldQueue.add(monthTextField);
                }
                case "y" -> {
                    yearLabel.putClientProperty("constraints", m("panel.date.field.year.constraints"));
                    yearTextField.putClientProperty("constraints", m("panel.date.field.year.constraints"));
                    dateLabelQueue.add(yearLabel);
                    dateFieldQueue.add(yearTextField);
                }
                default -> throw new UnexpectedFieldException(format("%s is not a valid field", field));
            }
        }

        var label = dateLabelQueue.poll();
        add(label, format("%s, span 2", label.getClientProperty("constraints")));

        label = dateLabelQueue.poll();
        add(label, format("%s, span 2", label.getClientProperty("constraints")));

        label = dateLabelQueue.poll();
        add(label, format("%s, wrap", label.getClientProperty("constraints")));

        var field = dateFieldQueue.poll();
        add(field, field.getClientProperty("constraints"));
        add(new JLabel(m("date.ui.fields.separator")), "");

        field = dateFieldQueue.poll();
        add(field, field.getClientProperty("constraints"));
        add(new JLabel(m("date.ui.fields.separator")), "");

        field = dateFieldQueue.poll();
        add(field, format("%s, wrap", field.getClientProperty("constraints")));
    }

    public DatePanel() {
        this(LocalDate.now());
    }

    public LocalDate getLocalDate() {
        if (!invalidTextFields.isEmpty()) {
            throw new InvalidDateValuesException(format("could not create LocalTime object for given time values, year: %s, month: %s, day: %s",
                    yearTextField.getText(), monthTextField.getText(), dayTextField.getText()));
        }
        return LocalDate.of(Integer.parseInt(yearTextField.getText()), Integer.parseInt(monthTextField.getText()), Integer.parseInt(dayTextField.getText()));
    }

    public void onChange(Runnable changeNotifier) {
        yearTextField.addKeyListener(createChangeNotifierKeyListener(changeNotifier));
        monthTextField.addKeyListener(createChangeNotifierKeyListener(changeNotifier));
        dayTextField.addKeyListener(createChangeNotifierKeyListener(changeNotifier));
    }

    public boolean isValidDate() {
        try {
            getLocalDate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
