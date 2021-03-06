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

package yottabox.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.filter.Filter;
import yottabox.hbasemodel.ScannerModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScannerResource extends ResourceBase {

    private static final Log LOG = LogFactory.getLog(ScannerResource.class);

    static final Map<String, ScannerInstanceResource> scanners =
            Collections.synchronizedMap(new HashMap<String, ScannerInstanceResource>());

    String tableName;

    /**
     * Constructor
     *
     * @param table
     * @throws java.io.IOException
     */
    public ScannerResource(String table) throws IOException {
        super();
        this.tableName = table;
    }

    static void delete(final String id) {
        ScannerInstanceResource instance = scanners.remove(id);
        if (instance != null) {
            instance.generator.close();
        }
    }

    Response update(final ScannerModel model, final boolean replace,
                    final UriInfo uriInfo) {
        servlet.getMetrics().incrementRequests(1);
        byte[] endRow = model.hasEndRow() ? model.getEndRow() : null;
        RowSpec spec = new RowSpec(model.getStartRow(), endRow,
                model.getColumns(), model.getStartTime(), model.getEndTime(), 1);
        try {
            Filter filter = ScannerResultGenerator.buildFilterFromModel(model);
            ScannerResultGenerator gen =
                    new ScannerResultGenerator(tableName, spec, filter);
            String id = gen.getID();
            ScannerInstanceResource instance =
                    new ScannerInstanceResource(tableName, id, gen, model.getBatch());
            scanners.put(id, instance);
            if (LOG.isDebugEnabled()) {
                LOG.debug("new scanner: " + id);
            }
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            URI uri = builder.path(id).build();
            return Response.created(uri).build();
        } catch (IOException e) {
            throw new WebApplicationException(e,
                    Response.Status.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    @PUT
    @Consumes({MIMETYPE_XML, MIMETYPE_JSON})
    public Response put(final ScannerModel model,
                        final @Context UriInfo uriInfo) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("PUT " + uriInfo.getAbsolutePath());
        }
        return update(model, true, uriInfo);
    }

    @POST
    @Consumes({MIMETYPE_XML, MIMETYPE_JSON})
    public Response post(final ScannerModel model,
                         final @Context UriInfo uriInfo) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uriInfo.getAbsolutePath());
        }
        return update(model, false, uriInfo);
    }

    @Path("{scanner: .+}")
    public ScannerInstanceResource getScannerInstanceResource(
            final @PathParam("scanner") String id) {
        ScannerInstanceResource instance = scanners.get(id);
        if (instance == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return instance;
    }
}
