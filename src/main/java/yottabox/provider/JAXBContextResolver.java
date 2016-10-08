/*
* Copyright 2010 The Apache Software Foundation
*
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

package yottabox.provider;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import yottabox.hbasemodel.*;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Plumbing for hooking up Jersey's JSON entity body encoding and decoding
 * support to JAXB. Modify how the context is created (by using e.g. a
 * different configuration builder) to control how JSON is processed and
 * created.
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    private final Set<Class<?>> types;

    private final Class<?>[] cTypes = {
            CellModel.class,
            CellSetModel.class,
            ColumnSchemaModel.class,
            RowModel.class,
            ScannerModel.class,
            StorageClusterStatusModel.class,
            StorageClusterVersionModel.class,
            TableInfoModel.class,
            TableListModel.class,
            TableModel.class,
            TableRegionModel.class,
            TableSchemaModel.class,
            VersionModel.class
    };

    @SuppressWarnings("unchecked")
    public JAXBContextResolver() throws Exception {
        this.types = new HashSet(Arrays.asList(cTypes));
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(),
                cTypes);
    }


    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }
}
