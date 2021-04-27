/*
 * Copyright 2020 me.yangbajing
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

package yangbajing.nacos4s.client.util

import com.alibaba.nacos.api.{ NacosFactory, PropertyKeyConst }
import com.typesafe.config.Config
import yangbajing.nacos4s.client.config.Nacos4sConfigService
import yangbajing.nacos4s.client.naming.Nacos4sNamingService

import java.util.Properties

object Nacos4s {
  def configService(servAddrList: String, namespace: String): Nacos4sConfigService = {
    val props = new Properties()
    props.setProperty(PropertyKeyConst.SERVER_ADDR, servAddrList)
    props.setProperty(PropertyKeyConst.NAMESPACE, namespace)
    configService(props)
  }

  def configService(config: Config): Nacos4sConfigService = configService(ConfigUtils.toProperties(config))

  def configService(props: Properties): Nacos4sConfigService =
    new Nacos4sConfigService(NacosFactory.createConfigService(props))

  def namingService(servAddrList: String, namespace: String): Nacos4sNamingService = {
    val props = new Properties()
    props.setProperty(PropertyKeyConst.SERVER_ADDR, servAddrList)
    props.setProperty(PropertyKeyConst.NAMESPACE, namespace)
    namingService(props)
  }

  def namingService(config: Config): Nacos4sNamingService =
    namingService(ConfigUtils.toProperties(config))

  def namingService(props: Properties): Nacos4sNamingService =
    new Nacos4sNamingService(NacosFactory.createNamingService(props), props)

  def namingService(serverList: String): Nacos4sNamingService =
    new Nacos4sNamingService(NacosFactory.createNamingService(serverList), new Properties())
}
