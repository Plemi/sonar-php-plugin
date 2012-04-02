/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.php.atoum.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * The Class ClassNode.
 */
@XStreamAlias("class")
public class ClassNode {

  /** The ignored node. */
  @XStreamOmitField
  @XStreamAlias("metrics")
  private Object ignoredNode;

  /** The lines. */
  @XStreamImplicit(itemFieldName = "line")
  private List<LineNode> lines;
  
  /**
   * @return the ignoredNode
   */
  public final Object getIgnoredNode() {
    return ignoredNode;
  }

  /**
   * @param ignoredNode
   *          the ignoredNode to set
   */
  public final void setIgnoredNode(Object ignoredNode) {
    this.ignoredNode = ignoredNode;
  }

  /**
   * Gets the lines.
   * 
   * @return the lines
   */
  public List<LineNode> getLines() {
    return lines;
  }

  /**
   * Sets the lines.
   * 
   * @param lines
   *          the new lines
   */
  public void setLines(List<LineNode> lines) {
    this.lines = lines;
  }
  
}
