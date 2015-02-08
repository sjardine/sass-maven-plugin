/*
 * Copyright 2015 Mark Prins, GeoDienstenCentrum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.geodienstencentrum.maven.plugin.sass;

/**
 * holds some constants useful for testing.
 * 
 * @author mark
 */
public enum TestConstantsEnum {
	/** The groupId of the test project. */
	TEST_GROUPID("nl.geodienstencentrum.maven.sass-maven-plugin"),
	/** The version of the test project. */
	TEST_VERSION("1.0-SNAPSHOT");
	
	/** our value. */
	private final String constant;
	
	/** construct a new instance with specified value.*/
	TestConstantsEnum(final String constant){
		this.constant = constant;
	}
	
	@Override
	public String toString(){
		return this.constant;
	}
}
