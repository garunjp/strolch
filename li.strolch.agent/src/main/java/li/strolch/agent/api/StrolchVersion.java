/*
 * Copyright 2013 Robert von Burg <eitch@eitchnet.ch>
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
package li.strolch.agent.api;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Robert von Burg <eitch@eitchnet.ch>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StrolchVersion {

	public static final String BUILD_TIMESTAMP = "buildTimestamp";
	public static final String SCM_BRANCH = "scmBranch";
	public static final String SCM_REVISION = "scmRevision";
	public static final String ARTIFACT_VERSION = "artifactVersion";
	public static final String ARTIFACT_ID = "artifactId";
	public static final String GROUP_ID = "groupId";

	@XmlAttribute(name = GROUP_ID)
	private String groupId;
	@XmlAttribute(name = ARTIFACT_ID)
	private String artifactId;
	@XmlAttribute(name = ARTIFACT_VERSION)
	private String artifactVersion;
	@XmlAttribute(name = SCM_REVISION)
	private String scmRevision;
	@XmlAttribute(name = SCM_BRANCH)
	private String scmBranch;
	@XmlAttribute(name = BUILD_TIMESTAMP)
	private String buildTimestamp;

	public StrolchVersion() {
		// no-arg constructor for JAXB
	}

	/**
	 * @param properties
	 */
	public StrolchVersion(Properties properties) {
		this.groupId = properties.getProperty(GROUP_ID); //$NON-NLS-1$
		this.artifactId = properties.getProperty(ARTIFACT_ID); //$NON-NLS-1$
		this.artifactVersion = properties.getProperty(ARTIFACT_VERSION); //$NON-NLS-1$
		this.scmRevision = properties.getProperty(SCM_REVISION); //$NON-NLS-1$
		this.scmBranch = properties.getProperty(SCM_BRANCH); //$NON-NLS-1$
		this.buildTimestamp = properties.getProperty(BUILD_TIMESTAMP); //$NON-NLS-1$
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * @param artifactId
	 *            the artifactId to set
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	/**
	 * @return the artifactVersion
	 */
	public String getArtifactVersion() {
		return this.artifactVersion;
	}

	/**
	 * @param artifactVersion
	 *            the artifactVersion to set
	 */
	public void setArtifactVersion(String artifactVersion) {
		this.artifactVersion = artifactVersion;
	}

	/**
	 * @return the scmRevision
	 */
	public String getScmRevision() {
		return this.scmRevision;
	}

	/**
	 * @param scmRevision
	 *            the scmRevision to set
	 */
	public void setScmRevision(String scmRevision) {
		this.scmRevision = scmRevision;
	}

	/**
	 * @return the scmBranch
	 */
	public String getScmBranch() {
		return this.scmBranch;
	}

	/**
	 * @param scmBranch
	 *            the scmBranch to set
	 */
	public void setScmBranch(String scmBranch) {
		this.scmBranch = scmBranch;
	}

	/**
	 * @return the buildTimestamp
	 */
	public String getBuildTimestamp() {
		return this.buildTimestamp;
	}

	/**
	 * @param buildTimestamp
	 *            the buildTimestamp to set
	 */
	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}
}
