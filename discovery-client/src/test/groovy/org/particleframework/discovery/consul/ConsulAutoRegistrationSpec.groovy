/*
 * Copyright 2018 original authors
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
package org.particleframework.discovery.consul

import io.reactivex.Flowable
import org.particleframework.context.ApplicationContext
import org.particleframework.discovery.DiscoveryClient
import org.particleframework.discovery.ServiceInstance
import org.particleframework.discovery.consul.client.v1.ConsulClient
import org.particleframework.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.PollingConditions

/**
 * @author graemerocher
 * @since 1.0
 */
@IgnoreIf({ !System.getenv('CONSUL_HOST') && !System.getenv('CONSUL_PORT')})
@Stepwise
class ConsulAutoRegistrationSpec extends Specification {


    void 'test that the service is automatically registered with Consul with a TTL configuration'() {
        when:"A new server is bootstrapped"
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
                ['particle.application.name':'test-auto-reg',
                 'consul.host': System.getenv('CONSUL_HOST'),
                 'consul.port': System.getenv('CONSUL_PORT')])
        ConsulClient client = embeddedServer.applicationContext.getBean(ConsulClient)
        DiscoveryClient discoveryClient = ApplicationContext.run(DiscoveryClient, ['consul.host':System.getenv('CONSUL_HOST')])

        PollingConditions conditions = new PollingConditions(timeout: 3)

        then:"the server is registered with Consul"
        conditions.eventually {
            List<ServiceInstance> instances = Flowable.fromPublisher(discoveryClient.getInstances('test-auto-reg')).blockingFirst()
            instances.size() == 1
            instances[0].port == embeddedServer.getPort()
            instances[0].host == embeddedServer.getHost()
        }

        when:"the server is shutdown"
        embeddedServer.stop()

        then:'the client is deregistered'

        conditions.eventually {
            List<ServiceInstance> instances = Flowable.fromPublisher(discoveryClient.getInstances('test-auto-reg')).blockingFirst()
            instances.size() == 0
        }
    }

    void 'test that the service is automatically registered with Consul with a HTTP configuration'() {
        when:"A new server is bootstrapped"
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer,
                ['particle.application.name':'test-auto-reg',
                 'consul.registration.check.http': true,
                 'consul.host': System.getenv('CONSUL_HOST'),
                 'consul.port': System.getenv('CONSUL_PORT')])
        ConsulClient client = embeddedServer.applicationContext.getBean(ConsulClient)
        DiscoveryClient discoveryClient = ApplicationContext.run(DiscoveryClient, ['consul.host':System.getenv('CONSUL_HOST')])

        PollingConditions conditions = new PollingConditions(timeout: 3)

        then:"the server is registered with Consul"
        conditions.eventually {
            List<ServiceInstance> instances = Flowable.fromPublisher(discoveryClient.getInstances('test-auto-reg')).blockingFirst()
            instances.size() == 1
            instances[0].port == embeddedServer.getPort()
            instances[0].host == embeddedServer.getHost()
        }

        when:"the server is shutdown"
        embeddedServer.stop()

        then:'the client is deregistered'

        conditions.eventually {
            List<ServiceInstance> instances = Flowable.fromPublisher(discoveryClient.getInstances('test-auto-reg')).blockingFirst()
            instances.size() == 0
        }
    }
}