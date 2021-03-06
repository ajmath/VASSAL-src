/*
 * $Id: HotKeyConfigurer.java 7738 2011-08-03 18:38:35Z uckelman $
 *
 * Copyright (c) 2000-2003 by Rodney Kinney
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package VASSAL.configure;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import VASSAL.i18n.Resources;

/**
 * A Configurer for {@link KeyStroke} values
 */
public class HotKeyConfigurer extends Configurer implements KeyListener {
  private JTextField tf;
  private JPanel p;

  public HotKeyConfigurer(String key, String name) {
    this(key, name, KeyStroke.getKeyStroke((char) 0));
  }

  public HotKeyConfigurer(String key, String name, KeyStroke val) {
    super(key, name, val);
  }

  public void setValue(Object o) {
    super.setValue(o);
    if (tf != null
        && !tf.getText().equals(keyToString())) {
      tf.setText(keyToString());
    }
  }

  public String keyToString() {
    return getString((KeyStroke) getValue());
  }

  public String getValueString() {
    return encode((KeyStroke) getValue());
  }

  public void setValue(String s) {
    setValue(s == null ? null : decode(s));
  }

  public java.awt.Component getControls() {
    if (p == null) {
      p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
      tf = new JTextField(8);
      tf.setMaximumSize(new Dimension(tf.getMaximumSize().width,tf.getPreferredSize().height));
      tf.setText(keyToString());
      tf.addKeyListener(this);
      p.add(new JLabel(getName()));
      p.add(tf);
    }
    return p;
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_DELETE:
      case KeyEvent.VK_BACK_SPACE:
        setValue(null);
        break;
      case KeyEvent.VK_SHIFT:
      case KeyEvent.VK_CONTROL:
      case KeyEvent.VK_META:
      case KeyEvent.VK_ALT:
        break;
      default:
        setValue(KeyStroke.getKeyStrokeForEvent(e));
    }
  }

  public void keyReleased(KeyEvent e) {
    tf.setText(getString((KeyStroke) getValue()));
  }

  /**
   * A plain text representation of a KeyStroke.  Doesn't differ much
   * from {@link KeyEvent#getKeyText}
   */
  public static String getString(KeyStroke k) {
    if (k == null) {
      return null;
    }
    String s = KeyEvent.getKeyText(k.getKeyCode());
    s = s.replace(' ', '_');
    if ((k.getModifiers() & KeyEvent.SHIFT_MASK) > 0) {
      s = Resources.getString("Keys.shift") + " " + s; //$NON-NLS-1$ //$NON-NLS-2$
    }
    if ((k.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
      s = Resources.getString("Keys.ctrl") + " " + s; //$NON-NLS-1$ //$NON-NLS-2$
    }
    if ((k.getModifiers() & KeyEvent.META_MASK) > 0) {
      s = Resources.getString("Keys.meta") + " " + s; //$NON-NLS-1$ //$NON-NLS-2$
    }
    if ((k.getModifiers() & KeyEvent.ALT_MASK) > 0) {
      s = Resources.getString("Keys.alt") + " " + s; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return s.toUpperCase();
  }

  /**
   * Decode a String into a KeyStroke
   */
  public static KeyStroke decode(String s) {
    final int index = s.indexOf(','); //$NON-NLS-1$
    if (index < 0) return null;

    try {
      return KeyStroke.getKeyStroke
          (Integer.parseInt(s.substring(0, index)),
           Integer.parseInt(s.substring(index + 1)));
    }
    // FIXME: review error message
    catch (NumberFormatException e) {
      return null;
    }
    catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Encode a KeyStroke into a String
   */
  public static String encode(KeyStroke stroke) {
    return stroke == null ? "" : stroke.getKeyCode() + "," + stroke.getModifiers(); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
