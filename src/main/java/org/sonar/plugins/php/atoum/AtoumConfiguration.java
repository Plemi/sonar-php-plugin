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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.php.core.AbstractPhpConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the php unit configuration.
 * 
 * @version 0.1 @author JTama
 * @version 0.3 @author Akram Ben Aissi
 */
public class AtoumConfiguration extends AbstractPhpConfiguration { 
  // --- Sonar config parameters ---
  private static final String ATOUM_COMMAND_LINE_KEY = "sonar.atoum.commandLine";
  private static final String ATOUM_COMMAND_LINE_DEFAULT = "atoum";
  
  public static final String ATOUM_ARGUMENT_LINE_KEY 		= "sonar.atoum.commandLineArgument";  
  public static final String ATOUM_ARGUMENT_LINE_DEFAULT	= "";
  
  public static final String ATOUM_REPORT_FILE_NAME_KEY 	= "sonar.atoum.reportFileName";
  public static final String ATOUM_REPORT_FILE_NAME_DEFAULT	= "atoum.xml";
  
  public static final String ATOUM_COVERAGE_REPORT_FILE_KEY 		= "sonar.atoum.coverageReportFileName";   
  public static final String ATOUM_COVERAGE_REPORT_FILE_DEFVALUE	= "atoum.coverage.xml";
  
  public static final String ATOUM_REPORT_FILE_RELATIVE_PATH_KEY 		= "sonar.atoum.reportFileRelativePath";
  public static final String ATOUM_REPORT_FILE_RELATIVE_PATH_DEFVALUE	= "/logs";
  
  public static final String  ATOUM_SHOULD_RUN_KEY		= "sonar.atoum.shouldRun";
  public static final boolean ATOUM_SHOULD_RUN_DEFAULT 	= true;
  public static final String  ATOUM_SKIP_KEY		= "sonar.atoum.shouldSkip";
  public static final boolean ATOUM_SKIP_DEFAULT	= true;
  
  public static final String  ATOUM_SHOULD_RUN_COVERAGE_KEY 	= "sonar.atoum.coverage.shouldRun";
  public static final boolean ATOUM_SHOULD_RUN_COVERAGE_DEFAULT	= true;
  public static final String  ATOUM_COVERAGE_SKIP_KEY 		= "sonar.atoum.coverage.shouldSkip";
  public static final boolean ATOUM_COVERAGE_SKIP_DEFAULT	= true;
  
  public static final String ATOUM_CONFIGURATION_KEY 		= "sonar.atoum.configuration";
  public static final String ATOUM_CONFIGURATION_DEFAULT	= "";
  
  public static final String ATOUM_BOOTSTRAP_KEY 		= "sonar.atoum.bootstrap";  
  public static final String ATOUM_BOOTSTRAP_DEFAULT	= "";
    
  public static final String ATOUM_ANALYZE_ONLY_KEY 		= "sonar.atoum.analyzeOnly";
  public static final String ATOUM_TIMEOUT_KEY 				= "sonar.atoum.timeout";
    
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AtoumExecutor.class);	
  
  private final Project project;
  
  public AtoumConfiguration(Project project) {
    super(project);
     
    Configuration configuration = project.getConfiguration();
    analyzeOnly = configuration.getBoolean(getShouldAnalyzeOnlyKey(), false);
    this.project = project; 
  }

  @Override
  protected String getCommandLine() {    
    return getProject().getConfiguration().getString(ATOUM_COMMAND_LINE_KEY, ATOUM_COMMAND_LINE_DEFAULT);
  }
  
  @Override
  protected String getArgumentLineKey() {
    return ATOUM_ARGUMENT_LINE_KEY;
  }
  
  @Override
  protected String getDefaultArgumentLine() {
    return ATOUM_ARGUMENT_LINE_DEFAULT;
  }

  @Override
  protected String getDefaultReportFileName() {
    return ATOUM_REPORT_FILE_NAME_DEFAULT;
  }

  @Override
  protected String getDefaultReportFilePath() {
    return ATOUM_REPORT_FILE_RELATIVE_PATH_DEFVALUE;
  }

  @Override
  protected String getReportFileNameKey() {
    return ATOUM_REPORT_FILE_NAME_KEY;
  }

  @Override
  protected String getReportFileRelativePathKey() {
    return ATOUM_REPORT_FILE_RELATIVE_PATH_KEY;
  }

  @Override
  protected final String getShouldAnalyzeOnlyKey() {
    return ATOUM_ANALYZE_ONLY_KEY;
  }

  @Override
  protected String getShouldRunKey() {
    return ATOUM_SHOULD_RUN_KEY;
  }

  @Override
  protected String getSkipKey() {
    return ATOUM_SKIP_KEY;
  }

  @Override
  protected String getTimeoutKey() {
    return ATOUM_TIMEOUT_KEY;
  }

  public List<String> getTestDirs() {	  
	  List<String> testDirs = new ArrayList<String>();
	  
	  List<File> projectTestDirs = project.getFileSystem().getTestDirs();	  
	  for(int i = 0; i < projectTestDirs.size(); i++) {
		  testDirs.add(projectTestDirs.get(i).toString());
	  }
	  
	  return testDirs;
  }
  
  /**
   * Gets the coverage report file.
   * 
   * @return the coverage report file
   */
  public File getCoverageReportFile() {
    Configuration configuration = getProject().getConfiguration();
    
    return new File(
    		getProject().getFileSystem().getBuildDir(), 
    		new StringBuilder().append(getReportFileRelativePath())
    			.append(File.separator)
    			.append(configuration.getString(ATOUM_COVERAGE_REPORT_FILE_KEY, ATOUM_COVERAGE_REPORT_FILE_DEFVALUE))
    			.toString()
	);
  }  
  
  /**
   * Gets the user defined configuration file.
   * 
   * @return the user defined configuration file.
   */
  public String getConfiguration() {
	  return getProject().getConfiguration().getString(ATOUM_CONFIGURATION_KEY, ATOUM_CONFIGURATION_DEFAULT);
  }
  
  /**
   * Gets the user defined boot strap.
   * 
   * @return the user defined filter.
   */
  public String getBootstrap() {
    return getProject().getConfiguration().getString(ATOUM_BOOTSTRAP_KEY, ATOUM_BOOTSTRAP_DEFAULT);
  }
  
  /**
   * Should run coverage.
   * 
   * @return true, if successful
   */
  public boolean shouldSkip() {
    return (
    		getProject().getConfiguration().getBoolean(ATOUM_SKIP_KEY, ATOUM_SKIP_DEFAULT) &&
    		!getProject().getConfiguration().getBoolean(ATOUM_SHOULD_RUN_KEY, ATOUM_SHOULD_RUN_DEFAULT)
	);
  }
  
  /**
   * Should run coverage.
   * 
   * @return true, if successful
   */
  public boolean shouldSkipCoverage() {
    return (
    		shouldSkip() || (
    				getProject().getConfiguration().getBoolean(ATOUM_COVERAGE_SKIP_KEY, ATOUM_COVERAGE_SKIP_DEFAULT) &&
    	    		!getProject().getConfiguration().getBoolean(ATOUM_SHOULD_RUN_COVERAGE_KEY, ATOUM_SHOULD_RUN_COVERAGE_DEFAULT)
			)    		
	);
  }
}
