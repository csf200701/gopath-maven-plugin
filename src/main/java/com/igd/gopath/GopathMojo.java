package com.igd.gopath;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "build", defaultPhase = LifecyclePhase.PACKAGE)
public class GopathMojo extends AbstractMojo {
	
	@Parameter(property = "gopath.sourceDir", required = true, defaultValue = "${project.build.directory}")
	private File sourceDir;

	@Parameter(property = "gopath.sourceJar", required = true, defaultValue = "${project.build.finalName}.jar")
	private String sourceJar;

	@Parameter(property = "gopath.targetDirs", required = true)
	private File[] targetDirs;

	@Parameter(property = "gopath.targetJar", required = true, defaultValue = "${project.build.finalName}.jar")
	private String targetJar;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if(targetDirs != null && targetDirs.length > 0) {
			try (BufferedInputStream ins = new BufferedInputStream(new FileInputStream(new File(this.sourceDir , this.sourceJar)))) {
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
		        int len = ins.read(bytes, 0, bytes.length);
		        while(len != -1){
		        	dataOut.write(bytes, 0, len);
		            len = ins.read(bytes, 0, bytes.length);
		        }
		        byte[] barr = dataOut.toByteArray();
		        dataOut.close();
		        
		        BufferedOutputStream ous = null;
		        for(File targetDir : targetDirs) {
		        	if(!targetDir.exists()) {
		        		targetDir.mkdirs();
		        	}
		        	ous = new BufferedOutputStream(new FileOutputStream(new File(targetDir , this.targetJar)));
		        	ous.write(barr);
		        	ous.flush();
		        	ous.close();
		        }
			} catch (Exception e) {
				throw new MojoExecutionException("Gopath execute fail", e);
			}	
		}
	}

}
