package com.tssap.dtr.client.lib.protocol.entities;



/**
 * This class provides the details of a collision between resources in an XCM workspace.
 * Instances of this class are used in the responses of CheckinRequest and
 * IntegrateRequest.
 */

// OK: should be actually in XCM. But due to high complexity
// of separation of states in the state machine of SAX parser
// (see MultiStatusEntity) it stays here
public class Collision {

	/** The workspace version with which this resource collides */
	private String workspaceVersion;

	/** The vcr with which this resource collides */
	private String vcr;

	/** The original version of this resource (optional) */
	private String originalVersion;

	/** The type of the collision (optional) */
	private String collisionType;

	/**
	 * Retrieves the workspace version associated with this collision.
	 * @return The path of a workspace version.
	 */
	public String getWorkspaceVersion() {
		return workspaceVersion;
	}

	/**
	 * Retrieves the VCR associated with this collision.
	 * @return The path of a VCR.
	 */
	public String getVCR() {
		return vcr;
	}

	/**
	 * Retrieves the original version associated with this collision.
	 * @return The path of a version.
	 */
	public String getOriginalVersion() {
		return originalVersion;
	}

	/**
	 * Retrieves the collision type parameter associated with this collision.
	 * @return The type of the collision, e.g. "Cyclic Discard".
	 */
	public String getCollisionType() {
		return collisionType;
	}

	/**
	 * Sets the active version parameter of the resource.
	 * @return The path of an active version.
	 */
	void setWorkspaceVersion(String workspaceVersion) {
		this.workspaceVersion = workspaceVersion;
	}

	/**
	 * Sets the vcr parameter of the resource.
	 * @return The path of a VCR.
	 */
	void setVCR(String vcr) {
		this.vcr = vcr;
	}

	/**
	 * Sets the original version parameter of the resource.
	 * @return The path of a version.
	 */
	void setOriginalVersion(String originalVersion) {
		this.originalVersion = originalVersion;
	}

	/**
	 * Sets the collision type parameter of the resource
	 * @return A collision type identifier.
	 */
	void setCollisionType(String collisionType) {
		this.collisionType = collisionType;
	}
}
