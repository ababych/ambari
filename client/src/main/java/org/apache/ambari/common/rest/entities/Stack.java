/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.common.rest.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StackType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StackType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/> 
 *         &lt;element name="DefaultBluePrint" type="{}BlueprintType"/>
 *         &lt;element name="Revision" type="{}String"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StackType", propOrder = {
    "name",
    "description",
    "locationURL"
})
@XmlRootElement(name = "Stack")
public class Stack {
	
    @XmlElement(name = "Name", required = true)
    protected String name;
	@XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "LocationURL", required = true)
    protected String locationURL;
    
    private HashMap<String, List<Blueprint>> blueprints = new HashMap<String, List<Blueprint>>();
   
    /*
     * Get blueprint
     */
    public Blueprint getBlueprint(String blueprintName, int revision) throws Exception {
    	if (!blueprints.containsKey(blueprintName) || revision < 0 || revision >= blueprints.get(blueprintName).size()
    		|| blueprints.get(blueprintName).get(revision) == null) {
    		throw new Exception ("Specified revision ["+revision+"] of ["+blueprintName+"] blueprint does not exists");
    	}
    	return blueprints.get(blueprintName).get(revision);  	
    }
    
    
    /*
     * Add or update the blueprint
     */
    public void putBlueprint(String blueprintName, Blueprint blueprint) {
    	if (!blueprints.containsKey(blueprintName)) {
    		blueprints.put(blueprintName, new ArrayList<Blueprint>());
    	}
    	blueprints.get(blueprintName).add(blueprint);    
    }
    
    /*
     * Delete the specified version of blueprint
     */
    public void deleteBlueprint(String blueprintName, int revision) throws Exception {
    	if (!blueprints.containsKey(blueprintName) || revision < 0 || revision >= blueprints.get(blueprintName).size()) {
    		throw new Exception ("Specified revision ["+revision+"] of ["+blueprintName+"] blueprint does not exists");
    	}
    	// This would change 
    	blueprints.get(blueprintName).set(revision, null);  	
    }
    
    /**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

	/**
	 * @return the defaultBlueprintDownloadURI
	 */
	public String getLocationURL() {
		return locationURL;
	}


	/**
	 * @param locationURL the locationURL to set
	 */
	public void setLocationURL(String locationURL) {
		this.locationURL = locationURL;
	}
}
