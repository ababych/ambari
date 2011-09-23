/**
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
package org.apache.ambari.components;

import java.io.IOException;
import java.util.List;

import org.apache.ambari.common.rest.entities.agent.Command;

/**
 * A plug in that defines how to manage each component.
 * 
 * All commands must be idempotent, so that if they are replayed, they
 * work without failure.
 */
public abstract class ComponentPlugin {
  
  /**
   * Get the roles for this component.
   * @return the list of roles in the order that they should be started
   * @throws IOException
   */
  public abstract String[] getRoles() throws IOException;
  
  /**
   * Get the components that this one depends on.
   * @return the list of components that must be installed for this one
   * @throws IOException
   */
  public abstract String[] getRequiredComponents() throws IOException;
  
  /**
   * Is this component a service (ie. runs servers)?
   * @return true if it has running servers
   * @throws IOException
   */
  public abstract boolean isService() throws IOException;
  
  /**
   * Get the commands to write the configuration for this component.
   * @param cluster the cluster that is being configured
   * @return the list of commands to run on each node
   * @throws IOException
   */
  public abstract List<Command> writeConfiguration(ClusterContext cluster
                                                   ) throws IOException;
  
  /**
   * Get the commands to finalize the installation on the machine.
   * @param cluster the cluster that is being installed
   * @return the list of commands to execute
   * @throws IOException
   */
  public abstract List<Command> install(ClusterContext cluster
                                        ) throws IOException;
  
  /**
   * Get the commands start and stop the servers on this node.
   * @param cluster the cluster that is being installed
   * @return the list of commands to execute
   * @throws IOException
   */
  public abstract List<Command> startStopServices(ClusterContext cluster
                                                  ) throws IOException;
  
  /**
   * Get the commands to run before the software is uninstalled.
   * @param cluster the cluster that is being uninstalled
   * @return the list of commands to execute
   * @throws IOException
   */
  public abstract List<Command> uninstall(ClusterContext cluster
                                          ) throws IOException;
}
