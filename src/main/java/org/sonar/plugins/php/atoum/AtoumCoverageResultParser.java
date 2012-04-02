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
package org.sonar.plugins.php.atoum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.php.atoum.xml.CoverageNode;
import org.sonar.plugins.php.atoum.xml.FileNode;
import org.sonar.plugins.php.atoum.xml.LineNode;
import org.sonar.plugins.php.atoum.xml.MetricsNode;
import org.sonar.plugins.php.atoum.xml.PackageNode;
import org.sonar.plugins.php.atoum.xml.ProjectNode;

import com.thoughtworks.xstream.XStream;

/**
 * The Class AtoumCoverageResultParser.
 */
public class AtoumCoverageResultParser implements BatchExtension {

  // Used for debugging purposes to store measure by resource
  private static final Map<Resource<?>, Measure> MEASURES_BY_RESOURCE = new HashMap<Resource<?>, Measure>();

  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(AtoumCoverageResultParser.class);

  /** The project. */
  private Project project;

  /** The context. */
  private SensorContext context;

  /**
   * Instantiates a new php unit coverage result parser.
   * 
   * @param project
   *          the project
   * @param context
   *          the context
   */
  public AtoumCoverageResultParser(Project project, SensorContext context) {
    super();
    this.project = project;
    this.context = context;
  }

  /**
   * Parses the.
   * 
   * @param coverageReportFile
   *          the coverage report file
   */
  public void parse(File coverageReportFile) {
    if (coverageReportFile != null) {
      LOG.info("Parsing file: " + coverageReportFile.getAbsolutePath());
      parseFile(coverageReportFile);
    } else {
    	LOG.info("Not Parsing coverage file");
    }
  }

  /**
   * Parses the file.
   * 
   * @param coverageReportFile
   *          the coverage report file
   */
  private void parseFile(File coverageReportFile) {
    CoverageNode coverage = getCoverage(coverageReportFile);

    List<ProjectNode> projects = coverage.getProjects();
    if (projects != null && !projects.isEmpty()) {
      ProjectNode projectNode = projects.get(0);
      parseFileNodes(projectNode.getFiles());
      parsePackagesNodes(projectNode.getPackages());
    }
  }

  private void parsePackagesNodes(List<PackageNode> packages) {
    if (packages != null && !packages.isEmpty()) {
      for (PackageNode packageNode : packages) {
        parseFileNodes(packageNode.getFiles());
      }
    }
  }

  private void parseFileNodes(List<FileNode> fileNodes) {
    if (fileNodes != null) {
      for (FileNode file : fileNodes) {
        saveCoverageMeasure(file);
      }
    }
  }

  /**
   * Saves the required metrics found on the fileNode
   * 
   * @param fileNode
   *          the file
   */
  protected void saveCoverageMeasure(FileNode fileNode) {
    File file = new File(fileNode.getPath());
    org.sonar.api.resources.File phpFile = org.sonar.api.resources.File.fromIOFile(file, project);    

    if (phpFile != null && fileNode.getLines() != null) {
      PropertiesBuilder<Integer, Integer> lineHits = new PropertiesBuilder<Integer, Integer>(CoreMetrics.COVERAGE_LINE_HITS_DATA); 
      
      for (LineNode line : fileNode.getLines()) {
    	  saveLineMeasure(line, lineHits);
      }
      
      MetricsNode metrics = fileNode.getMetrics();
      Measure measure = lineHits.build();
      logMeasureByResource(phpFile, measure);
      context.saveMeasure(phpFile, measure);
      
      // Save uncovered statements (lines)
      double totalStatementsCount = metrics.getTotalStatementsCount();
      double uncoveredLines = totalStatementsCount - metrics.getCoveredStatements();
      double lineCoverage = 0;
      if (metrics.getCoveredStatements() != 0) {
        lineCoverage = metrics.getCoveredStatements() / totalStatementsCount;
      }
      context.saveMeasure(phpFile, CoreMetrics.LINES_TO_COVER, totalStatementsCount);
      context.saveMeasure(phpFile, CoreMetrics.UNCOVERED_LINES, uncoveredLines);
      context.saveMeasure(phpFile, CoreMetrics.LINE_COVERAGE, ParsingUtils.scaleValue(lineCoverage * 100.0));
    }
  }

  private void logMeasureByResource(org.sonar.api.resources.File phpFile, Measure measure) {
    if (LOG.isDebugEnabled()) {
      Measure alreadySaved = MEASURES_BY_RESOURCE.get(phpFile);
      if (alreadySaved == null) {
        MEASURES_BY_RESOURCE.put(phpFile, measure);
      } else {
        LOG.info("Measure {} already saved for resoruce {}", measure, phpFile);
      }
    }
  }

  /**
   * Save line measure.
   * 
   * @param line
   *          the line
   * @param lineHits
   *          the line hits
   */
  private void saveLineMeasure(LineNode line, PropertiesBuilder<Integer, Integer> lineHits) {
    lineHits.add(line.getNum(), line.getCount());
  }

  /**
   * Gets the coverage.
   * 
   * @param coverageReportFile
   *          the coverage report file
   * @return the coverage
   */
  private CoverageNode getCoverage(File coverageReportFile) {
    InputStream inputStream = null;
    try {
      XStream xstream = new XStream();
      xstream.setClassLoader(getClass().getClassLoader());
      xstream.aliasSystemAttribute("classType", "class");
      xstream.processAnnotations(CoverageNode.class);
      xstream.processAnnotations(ProjectNode.class);
      xstream.processAnnotations(FileNode.class);
      xstream.processAnnotations(MetricsNode.class);
      xstream.processAnnotations(LineNode.class);
      inputStream = new FileInputStream(coverageReportFile);
      return (CoverageNode) xstream.fromXML(inputStream);
    } catch (IOException e) {
      throw new SonarException("Can't read pUnit report : " + coverageReportFile.getName(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

}
