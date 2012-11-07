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

package org.apache.ambari.server.orm.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import org.apache.ambari.server.Role;
import org.apache.ambari.server.actionmanager.HostRoleStatus;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostRoleCommandEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.Collection;
import java.util.List;

public class HostRoleCommandDAO {

  @Inject
  Provider<EntityManager> entityManagerProvider;
  @Inject
  DaoUtils daoUtils;
  private static Logger LOG = LoggerFactory.getLogger(HostRoleCommandDAO.class);

  @Transactional
  public HostRoleCommandEntity findByPK(long taskId) {
    return entityManagerProvider.get().find(HostRoleCommandEntity.class, taskId);
  }

  @Transactional
  public List<HostRoleCommandEntity> findByPKs(Collection<Long> taskIds) {
    LOG.info("HostRole Command Entity  " + entityManagerProvider.get());
    TypedQuery<HostRoleCommandEntity> query = entityManagerProvider.get().createQuery(
        "SELECT task FROM HostRoleCommandEntity task WHERE task.taskId IN ?1 " +
            "ORDER BY task.taskId",
        HostRoleCommandEntity.class);
    return daoUtils.selectList(query, taskIds);
  }

  @Transactional
  public List<HostRoleCommandEntity> findSortedCommandsByStageAndHost(StageEntity stageEntity, HostEntity hostEntity) {
    TypedQuery<HostRoleCommandEntity> query = entityManagerProvider.get().createQuery("SELECT hostRoleCommand " +
        "FROM HostRoleCommandEntity hostRoleCommand " +
        "WHERE hostRoleCommand.stage=?1 AND hostRoleCommand.host=?2 " +
        "ORDER BY hostRoleCommand.taskId", HostRoleCommandEntity.class);
    return daoUtils.selectList(query, stageEntity, hostEntity);
  }

  @Transactional
  public List<HostRoleCommandEntity> findByHostRole(String hostName, long requestId, long stageId, Role role) {
    LOG.info("HostRole Command Entity  " + entityManagerProvider.get());
    TypedQuery<HostRoleCommandEntity> query = entityManagerProvider.get().createQuery("SELECT command " +
        "FROM HostRoleCommandEntity command " +
        "WHERE command.hostName=?1 AND command.requestId=?2 " +
        "AND command.stageId=?3 AND command.role=?4 " +
        "ORDER BY command.taskId", HostRoleCommandEntity.class);

    return daoUtils.selectList(query, hostName, requestId, stageId, role);
  }

  @Transactional
  public List<Long> getRequests() {
    String queryStr = "SELECT DISTINCT command.requestId " +
        "FROM HostRoleCommandEntity command ORDER BY command.requestId DESC";
    TypedQuery<Long> query = entityManagerProvider.get().createQuery(queryStr,
        Long.class);
    query.setMaxResults(20);
    return daoUtils.selectList(query);
  }

  @Transactional
  public int updateStatusByRequestId(long requestId, HostRoleStatus target, Collection<HostRoleStatus> sources) {
    Query query = entityManagerProvider.get().createQuery("UPDATE HostRoleCommandEntity command " +
        "SET command.status=?1 " +
        "WHERE command.requestId=?2 AND command.status IN ?3");

    return daoUtils.executeUpdate(query, target, requestId, sources);
  }

  @Transactional
  public List<HostRoleCommandEntity> findByRequest(long requestId) {
    TypedQuery<HostRoleCommandEntity> query = entityManagerProvider.get().createQuery("SELECT command " +
        "FROM HostRoleCommandEntity command " +
        "WHERE command.requestId=?1 ORDER BY command.taskId", HostRoleCommandEntity.class);
    return daoUtils.selectList(query, requestId);
  }

  @Transactional
  public void create(HostRoleCommandEntity stageEntity) {
    entityManagerProvider.get().persist(stageEntity);
  }

  @Transactional
  public HostRoleCommandEntity merge(HostRoleCommandEntity stageEntity) {
    HostRoleCommandEntity entity = entityManagerProvider.get().merge(stageEntity);
    return entity;

  }

  @Transactional
  public void remove(HostRoleCommandEntity stageEntity) {
    entityManagerProvider.get().remove(merge(stageEntity));
  }

  @Transactional
  public void removeByPK(int taskId) {
    remove(findByPK(taskId));
  }
}
