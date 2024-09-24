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

package com.webank.wedpr.core.protocol;

import org.apache.commons.lang3.StringUtils;

public enum JobType {
    PSI("PSI"),
    MPC("MPC"),
    ML_PSI("ML_PSI"),
    MLPreprocessing("PREPROCESSING"),
    FeatureEngineer("FEATURE_ENGINEERING"),
    XGB_TRAIN("XGB_TRAINING"),
    XGB_PREDICT("XGB_PREDICTING");

    private final String type;

    JobType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public boolean mlJob() {
        return (ordinal() == MLPreprocessing.ordinal())
                || (ordinal() == FeatureEngineer.ordinal())
                || (ordinal() == XGB_PREDICT.ordinal())
                || (ordinal() == XGB_TRAIN.ordinal());
    }

    public boolean predictJob() {
        return ordinal() == XGB_PREDICT.ordinal();
    }

    public boolean trainJob() {
        return ordinal() == XGB_TRAIN.ordinal();
    }

    public static Boolean isXGBJob(String jobType) {
        return jobType.compareToIgnoreCase(XGB_TRAIN.getType()) == 0
                || jobType.compareToIgnoreCase(XGB_PREDICT.getType()) == 0;
    }

    public static Boolean isPSIJob(String jobType) {
        return jobType.compareToIgnoreCase(PSI.getType()) == 0;
    }

    public static Boolean isMPCJob(String jobType) {
        return jobType.compareToIgnoreCase(MPC.getType()) == 0;
    }

    public static JobType deserialize(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        for (JobType jobType : JobType.values()) {
            if (jobType.type.compareToIgnoreCase(type) == 0) {
                return jobType;
            }
        }
        return null;
    }
}
