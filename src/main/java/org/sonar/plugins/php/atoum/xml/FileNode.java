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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * The Class FileNode.
 */
@XStreamAlias("file")
public class FileNode {
	  @XStreamAsAttribute
	  private String name;
	
	  @XStreamAsAttribute
	  private String path;
	  
	  @XStreamImplicit(itemFieldName = "line")
	  private List<LineNode> lines;

	  @XStreamImplicit(itemFieldName = "class")
	  @XStreamOmitField
	  private List<ClassNode> classes;

	  @XStreamAlias("metrics")
	  private MetricsNode metrics;

	  /**
	   * @param lines
	   *          the lines
	   * @param metrics
	   *          the metrics
	   * @param name
	   *          the name
	   */
	  public FileNode(List<LineNode> lines, MetricsNode metrics, String name, String path) {
		super();
		this.lines = lines;
		this.metrics = metrics;
		this.name = name;
		this.path = path;
	  }  

	  /**
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

	  /**
	   * Gets the metrics.
	   * 
	   * @return the metrics
	   */
	  public MetricsNode getMetrics() {
	    return metrics;
	  }

	  /**
	   * Sets the metrics.
	   * 
	   * @param metrics
	   *          the new metrics
	   */
	  public void setMetrics(MetricsNode metrics) {
		  this.metrics = metrics;
	  }

	  /**
	   * @return the name
	   */
	  public String getName() {
		  return name;
	  }

	  /**
	   * @return the ignoredNodes
	   */
	  public final List<ClassNode> getClasses() {
		  return this.classes;
	  }
	
	  /**
	   * @param ignoredNodes
	   *          the ignoredNodes to set
	   */
	  public final void setClasses(List<ClassNode> classes) {
		  this.classes = classes;
	  }
	
	  /**
	   * @param name
	   *          the new name
	   */
	  public void setName(String name) {
		  this.name = name;
	  }
	  
	  public String getPath() {
		  return this.path;
	  }
	  
	  public void setPath(String path) {
		  this.path = path;
	  }
}
