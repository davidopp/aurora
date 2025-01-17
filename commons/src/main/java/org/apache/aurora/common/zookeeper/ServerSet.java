/**
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
package org.apache.aurora.common.zookeeper;

import org.apache.aurora.common.net.pool.DynamicHostSet;
import org.apache.aurora.common.zookeeper.Group.JoinException;
import org.apache.aurora.common.thrift.ServiceInstance;
import org.apache.aurora.common.thrift.Status;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * A logical set of servers registered in ZooKeeper.  Intended to be used by both servers in a
 * common service and their clients.
 *
 * TODO(William Farner): Explore decoupling this from thrift.
 */
public interface ServerSet extends DynamicHostSet<ServiceInstance> {
  /**
   * Attempts to join a server set for this logical service group.
   *
   * @param endpoint the primary service endpoint
   * @param additionalEndpoints and additional endpoints keyed by their logical name
   * @return an EndpointStatus object that allows the endpoint to adjust its status
   * @throws JoinException if there was a problem joining the server set
   * @throws InterruptedException if interrupted while waiting to join the server set
   */
  EndpointStatus join(
      InetSocketAddress endpoint,
      Map<String, InetSocketAddress> additionalEndpoints)
      throws JoinException, InterruptedException;

  /**
   * Attempts to join a server set for this logical service group.
   *
   * @param endpoint the primary service endpoint
   * @param additionalEndpoints and additional endpoints keyed by their logical name
   * @param shardId Unique shard identifier for this member of the service.
   * @return an EndpointStatus object that allows the endpoint to adjust its status
   * @throws JoinException if there was a problem joining the server set
   * @throws InterruptedException if interrupted while waiting to join the server set
   */
  EndpointStatus join(
      InetSocketAddress endpoint,
      Map<String, InetSocketAddress> additionalEndpoints,
      int shardId) throws JoinException, InterruptedException;

  /**
   * A handle to a service endpoint's status data that allows updating it to track current events.
   */
  interface EndpointStatus {
    /**
     * Removes the endpoint from the server set.
     *
     * @throws UpdateException if there was a problem leaving the ServerSet.
     */
    void leave() throws UpdateException;
  }

  /**
   * Indicates an error updating a service's status information.
   */
  class UpdateException extends Exception {
    public UpdateException(String message, Throwable cause) {
      super(message, cause);
    }

    public UpdateException(String message) {
      super(message);
    }
  }
}
