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

import de.schipplock.gui.swing.lafmanager.LAFManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class DateTimePanelDemo extends JFrame {

    public DateTimePanelDemo() {
        setupFrame();
    }

    private void centerFrame() {
        GraphicsDevice screen = MouseInfo.getPointerInfo().getDevice();
        Rectangle r = screen.getDefaultConfiguration().getBounds();
        int x = (r.width - this.getWidth()) / 2 + r.x;
        int y = (r.height - this.getHeight()) / 2 + r.y;
        setLocation(x, y);
    }

    private void setupFrame() {
        setMinimumSize(new Dimension(300, 160));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new MigLayout());

        var dateTimePanel = new DateTimePanel();
        var datePanel = new DatePanel();
        var timePanel = new TimePanel();

        dateTimePanel.onChange(() -> {
            if (dateTimePanel.isValidDateTime()) {
                System.out.println("datetime value has changed to: " + dateTimePanel.getLocalDateTime().toString());
            }
        });

        datePanel.onChange(() -> {
            if(datePanel.isValidDate()) {
                System.out.println("date value has changed to: " + datePanel.getLocalDate().toString());
            }
        });

        timePanel.onChange(() -> {
            if (timePanel.isValidTime()) {
                System.out.println("time value has changed to: " + timePanel.getLocalTime().toString());
            }
        });

        getContentPane().add(new JLabel("<html>Ein <b>DateTimePanel</b>:</html>"), "wrap");
        getContentPane().add(dateTimePanel, "wrap");
        getContentPane().add(new JLabel("<html>Ein <b>DatePanel</b>:</html>"), "wrap");
        getContentPane().add(datePanel, "wrap");
        getContentPane().add(new JLabel("<html>Ein <b>TimePanel</b>:</html>"), "wrap");
        getContentPane().add(timePanel, "wrap");
        pack();

        centerFrame();
    }

    public static void createAndShowGui() {
        JFrame frame = new DateTimePanelDemo();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        LAFManager.create().setLookAndFeelByName("FlatLaf IntelliJ");
        SwingUtilities.invokeLater(DateTimePanelDemo::createAndShowGui);
    }
}
