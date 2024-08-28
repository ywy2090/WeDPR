/*
 * Copyright 2017-2025  [webank-wedpr]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.webank.wedpr.components.integration.jupyter.service.impl;

import com.webank.wedpr.core.config.WeDPRConfig;

public class JupyterConfig {

    private static Integer MAX_JUPYTER_PER_HOST =
            WeDPRConfig.apply("wedpr.jupyter.max_count_per_host", 3);
    private static String JUPYTER_HOST_CONFIGUATINON_KEY =
            WeDPRConfig.apply("wedpr.jupyter.host_configuration_key", "jupyter_entrypoints");
    private static String JUPYTER_ENTRYPOINT_SPLITTER = ";";

    public static String getJupyterHostConfiguatinonKey() {
        return JUPYTER_HOST_CONFIGUATINON_KEY;
    }

    public static Integer getMaxJupyterPerHost() {
        return MAX_JUPYTER_PER_HOST;
    }

    public static String getJupyterEntrypointSplitter() {
        return JUPYTER_ENTRYPOINT_SPLITTER;
    }
}