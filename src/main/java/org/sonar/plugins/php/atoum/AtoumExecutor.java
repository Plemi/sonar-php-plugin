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

import static org.sonar.plugins.php.api.Php.PHP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.InputFileUtils;
import org.sonar.api.resources.Project;
import org.sonar.plugins.php.core.AbstractPhpExecutor;

import com.google.common.collect.Lists;

/**
 * The Class AtoumExecutor.
 */
public class AtoumExecutor extends AbstractPhpExecutor {  
	// -- atoum tool options ---
	private static final String ATOUM_CONFIGURATION_OPTION = "--configuration-files ";
	private static final String ATOUM_TESTS_DIR_OPTION 	= "--directories ";
	private static final String ATOUM_BOOTSTRAP_OPTION 	= "--bootstrap-file ";
	private static final String ATOUM_MAXCHILDREN_OPTION 	= "--max-children-number ";
	private static final String ATOUM_SKIP_COVERAGE_OPTION	= "--no-code-coverage ";
	private static final String ATOUM_PREFIX = ".atoum";
	private static final String XML_SUFFIX = ".php";  
	
	private static final org.slf4j.Logger  LOG = LoggerFactory.getLogger(AtoumExecutor.class);	 
	private static final Collection<Integer> ACCEPTED_EXIT_CODES = Lists.newArrayList(0, 1);
  
	private final AtoumConfiguration configuration;
	private final Project project;

	public AtoumExecutor(AtoumConfiguration configuration, Project project) {
		super(configuration, ACCEPTED_EXIT_CODES);
    
		this.configuration = configuration;
		this.project = project;
		PHP.setConfiguration(configuration.getProject().getConfiguration());
	}
  
	@Override
	protected List<String> getCommandLine() {
		List<String> result = new ArrayList<String>();
		result.add(configuration.getOsDependentToolScriptName());      
        
		addExtendedOptions(result);
		addBasicOptions(result);        		   
    
		return result;
	}
  
	private void addExtendedOptions(List<String> result) {
		String argumentLine = configuration.getArgumentLine();	  
		LOG.info("Argument line : " + argumentLine);
		if(!argumentLine.equals("")) {
			result.add(new StringBuilder(argumentLine).append(" ").toString());      
		}       
	}
  
	private void addBasicOptions(List<String> result) {    	  	  	  	  
		result.add(ATOUM_TESTS_DIR_OPTION);
		
		List<String> directories = configuration.getTestDirs();		
		for(String directory : directories) {
			LOG.info("Test dir : " + directory);
			result.add(
				new StringBuilder(directory)
					.append(" ")
					.toString()
			);
		}
	  
		String configurationFile = configuration.getConfiguration();
		if(!configurationFile.equals("")) {
			result.add(
				new StringBuilder(ATOUM_CONFIGURATION_OPTION)
			  		.append(" ")
			  		.append(configurationFile)
			  		.append(" ")
			  		.toString()
			);      
		} 
	  
		String bootstrapFile = configuration.getBootstrap();
		if(!bootstrapFile.equals("")) {
			result.add(ATOUM_BOOTSTRAP_OPTION + configuration.getBootstrap());
			result.add(
				new StringBuilder(ATOUM_BOOTSTRAP_OPTION)
			  		.append(" ")
			  		.append(bootstrapFile)
			  		.append(" ")
			  		.toString()
				); 
		}  
	  
		if (configuration.shouldSkipCoverage()) {
			result.add(ATOUM_SKIP_COVERAGE_OPTION);
		}  	  	 
	}

	@Override
	protected String getExecutedTool() {
		return "atoum";
	}
	  
	public AtoumConfiguration getConfiguration() {
		return configuration;
	}
	
	
  private File createPhpunitConfigurationFile(List<File> testFiles) {
    File workingDir = configuration.createWorkingDirectory();
    File ruleset = null;
    try {
      ruleset = File.createTempFile(ATOUM_PREFIX, XML_SUFFIX, workingDir);
      StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      xml.append("<atoum><testsuites><testsuite name=\"Generated\">\n");
      for (File f : testFiles) {
        xml.append("<file>").append(f.getAbsolutePath()).append("</file>\n");
      }
      xml.append("</testsuite></testsuites></atoum>");
      FileUtils.writeStringToFile(ruleset, xml.toString());
    } catch (IOException e) {
      String msg = "Error while creating temporary " + ATOUM_PREFIX + "." + XML_SUFFIX + " from files: " + testFiles + " to file : " + ruleset + " in dir "
        + workingDir;
      
      LOG.error(msg);
    }
    return ruleset.length() > 0 ? ruleset : null;
  }
}
