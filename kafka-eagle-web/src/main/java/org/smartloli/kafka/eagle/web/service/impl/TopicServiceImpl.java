/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.kafka.eagle.web.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.smartloli.kafka.eagle.web.service.TopicService;

import java.util.List;
import java.util.Map;

import org.smartloli.kafka.eagle.common.protocol.MetadataInfo;
import org.smartloli.kafka.eagle.common.protocol.PartitionsInfo;
import org.smartloli.kafka.eagle.common.protocol.topic.TopicConfig;
import org.smartloli.kafka.eagle.common.util.KConstants.Kafka;
import org.smartloli.kafka.eagle.common.util.KConstants.Topic;
import org.smartloli.kafka.eagle.core.factory.KafkaFactory;
import org.smartloli.kafka.eagle.core.factory.KafkaService;
import org.smartloli.kafka.eagle.core.factory.v2.BrokerFactory;
import org.smartloli.kafka.eagle.core.factory.v2.BrokerService;
import org.smartloli.kafka.eagle.core.metrics.KafkaMetricsFactory;
import org.smartloli.kafka.eagle.core.metrics.KafkaMetricsService;
import org.smartloli.kafka.eagle.core.sql.execute.KafkaSqlParser;
import org.springframework.stereotype.Service;

/**
 * Kafka topic implements service interface.
 * 
 * @author smartloli.
 *
 *         Created by Aug 14, 2016.
 * 
 *         Update by hexiang 20170216
 */
@Service
public class TopicServiceImpl implements TopicService {

	/** Kafka service interface. */
	private KafkaService kafkaService = new KafkaFactory().create();

	/** Kafka topic config service interface. */
	private KafkaMetricsService kafkaMetricsService = new KafkaMetricsFactory().create();

	/** Broker service interface. */
	private static BrokerService brokerService = new BrokerFactory().create();

	/** Find topic name in all topics. */
	public boolean hasTopic(String clusterAlias, String topicName) {
		return brokerService.findKafkaTopic(clusterAlias, topicName);
	}

	/** Get metadata in topic. */
	public List<MetadataInfo> metadata(String clusterAlias, String topicName,Map<String, Object> params) {
		return brokerService.topicMetadataRecords(clusterAlias, topicName, params);
	}

	/** Execute kafka execute query sql and viewer topic message. */
	public String execute(String clusterAlias, String sql) {
		return KafkaSqlParser.execute(clusterAlias, sql);
	}

	/** Get kafka 0.10.x mock topics. */
	public String mockTopics(String clusterAlias, String name) {
		List<String> topicList = brokerService.topicList(clusterAlias);
		int offset = 0;
		JSONArray topics = new JSONArray();
		for (String topicName : topicList) {
			if (name != null) {
				JSONObject topic = new JSONObject();
				if (name.contains(topicName) && !topicName.equals(Kafka.CONSUMER_OFFSET_TOPIC)) {
					topic.put("text", topicName);
					topic.put("id", offset);
				}
				topics.add(topic);
			} else {
				JSONObject topic = new JSONObject();
				if (!topicName.equals(Kafka.CONSUMER_OFFSET_TOPIC)) {
					topic.put("text", topicName);
					topic.put("id", offset);
				}
				topics.add(topic);
			}

			offset++;
		}
		return topics.toJSONString();
	}

	/** Send mock message to kafka topic . */
	public boolean mockSendMsg(String clusterAlias, String topic, String message) {
		return kafkaService.mockMessage(clusterAlias, topic, message);
	}

	/** Get topic property keys */
	public String listTopicKeys(String clusterAlias, String name) {
		JSONArray topics = new JSONArray();
		int offset = 0;
		for (String key : Topic.KEYS) {
			if (name != null) {
				JSONObject topic = new JSONObject();
				if (key.contains(name)) {
					topic.put("text", key);
					topic.put("id", offset);
				}
				topics.add(topic);
			} else {
				JSONObject topic = new JSONObject();
				topic.put("text", key);
				topic.put("id", offset);
				topics.add(topic);
			}
			offset++;
		}
		return topics.toJSONString();
	}

	/** Alter topic config. */
	public String changeTopicConfig(String clusterAlias, TopicConfig topicConfig) {
		return kafkaMetricsService.changeTopicConfig(clusterAlias, topicConfig.getName(), topicConfig.getType(), topicConfig.getConfigEntry());
	}

	/** Get topic numbers. */
	public long getTopicNumbers(String clusterAlias) {
		return brokerService.topicNumbers(clusterAlias);
	}

	/** Get topic list. */
	public List<PartitionsInfo> list(String clusterAlias, Map<String, Object> params) {
		return brokerService.topicRecords(clusterAlias, params);
	}

	/** Get topic partition numbers. */
	public long getPartitionNumbers(String clusterAlias, String topic) {
		return brokerService.partitionNumbers(clusterAlias, topic);
	}
	
}
