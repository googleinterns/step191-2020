// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public final class Comment {

  private final long id;
  private final String body;
  private final String username;
  private final long timestamp;
  private final int upvotes;
  private final int downvotes;
  private final String nickname;

  public Comment(long id, String body, String username, long timestamp, int upvotes, int downvotes) {
    this.id = id;
    this.body = body;
    this.username = username;
    this.timestamp = timestamp;
    this.upvotes = upvotes;
    this.downvotes = downvotes;
    this.nickname = getUserNickname(username);
  }

  public Comment(Entity entity) {
    long id = entity.getKey().getId();
    String username = (String) entity.getProperty("commentUsername");
    String body = (String) entity.getProperty("commentBody");
    long timestamp = (long) entity.getProperty("timestamp");
    int upvotes = ((Long) entity.getProperty("upvotes")).intValue();
    int downvotes = ((Long) entity.getProperty("downvotes")).intValue();
    this.id = id;
    this.body = body;
    this.username = username;
    this.timestamp = timestamp;
    this.upvotes = upvotes;
    this.downvotes = downvotes;
    this.nickname = getUserNickname(username);
  }

  public Entity toEntity() {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("commentUsername", username);
    commentEntity.setProperty("commentBody", body);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("upvotes", upvotes);
    commentEntity.setProperty("downvotes", downvotes);

    return commentEntity;
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }

}
