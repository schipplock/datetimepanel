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

import de.schipplock.gui.swing.datetimepanel.listener.TextFieldKeyListener;
import de.schipplock.gui.swing.datetimepanel.verifier.Verifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import java.util.Set;

import static java.lang.String.format;

public interface Panel {

    Color defaultForeground = new JTextField().getForeground();

    default TextFieldKeyListener createTextFieldKeyListener(Set<JTextField> invalidTextFields, Verifier verifier) {
        return new TextFieldKeyListener(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return verifyTextField((JTextField) input, invalidTextFields, verifier);
            }
        }) {
            @Override
            public void keyReleased(KeyEvent e) {
                inputVerifier.verify((JTextField) e.getSource());
            }
        };
    }

    default KeyAdapter createChangeNotifierKeyListener(Runnable changeNotifier) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                changeNotifier.run();
            }
        };
    }

    default boolean verifyTextField(JTextField textField, Set<JTextField> invalidTextFields, Verifier verifier) {
        boolean verified = verifier.verify(textField.getText());
        if (!verified) {
            textField.setForeground(Color.RED);
            invalidTextFields.add(textField);
        } else {
            textField.setForeground(defaultForeground);
            invalidTextFields.remove(textField);
        }
        return verified;
    }

    default String m(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(format("%s/i18n/MessagesBundle", getClass().getPackageName().replace(".", "/")));
        return bundle.getString(key);
    }

    static String getLayoutConstraints() {
        var constraints = System.getenv("MIGLAYOUT_CONSTRAINTS");
        var layoutDefaults = "gap 0 0, ins 0 4 4 4";

        if (constraints == null) {
            constraints = layoutDefaults;
        } else {
            constraints = format("%s, %s", layoutDefaults, constraints);
        }

        return constraints;
    }
}
