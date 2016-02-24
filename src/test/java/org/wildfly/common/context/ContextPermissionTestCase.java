/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package org.wildfly.common.context;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class ContextPermissionTestCase {
    @Test
    public void testActions() {
        final ContextPermission allBlah = new ContextPermission("blah", "*");
        final ContextPermission allBaz = new ContextPermission("baz", "*");

        Assert.assertFalse(allBaz.implies(allBlah));
        Assert.assertFalse(allBlah.implies(allBaz));
        Assert.assertFalse(allBaz.equals(allBlah));
        Assert.assertFalse(allBlah.equals(allBaz));

        final ContextPermission justGetBlah = new ContextPermission("blah", "get,getGlobalDefault,getThreadDefault");

        Assert.assertTrue(allBlah.implies(justGetBlah));
        Assert.assertFalse(justGetBlah.implies(allBlah));
        Assert.assertFalse(allBlah.equals(justGetBlah));
        Assert.assertFalse(justGetBlah.equals(allBlah));

        Assert.assertFalse(allBaz.implies(justGetBlah));

        final ContextPermission justSetBlah = new ContextPermission("blah", "setGlobalDefault,setThreadDefault");

        Assert.assertTrue(allBlah.implies(justSetBlah));
        Assert.assertFalse(justSetBlah.implies(allBlah));
        Assert.assertFalse(justSetBlah.implies(justGetBlah));
        Assert.assertFalse(justGetBlah.implies(justSetBlah));

        final ContextPermission justGetAndSetBlah = justGetBlah.withActions(justSetBlah.getActions());

        Assert.assertTrue(allBlah.implies(justGetAndSetBlah));
        Assert.assertTrue(justGetAndSetBlah.implies(justGetBlah));
        Assert.assertTrue(justGetAndSetBlah.implies(justSetBlah));
    }

    @Test
    public void testNames() {
        final ContextPermission allAll = new ContextPermission("*", "*");
        final ContextPermission getAll = new ContextPermission("*", "get,getGlobalDefault,getThreadDefault");
        final ContextPermission allBlah = new ContextPermission("blah", "*");
        final ContextPermission allBaz = new ContextPermission("baz", "*");
        final ContextPermission justGetBlah = new ContextPermission("blah", "get,getGlobalDefault,getThreadDefault");
        final ContextPermission justSetBlah = new ContextPermission("blah", "setGlobalDefault,setThreadDefault");
        final ContextPermission justGetAndSetBlah = justGetBlah.withActions(justSetBlah.getActions());

        Assert.assertTrue(allAll.implies(allAll));
        Assert.assertTrue(allAll.implies(getAll));
        Assert.assertTrue(allAll.implies(allBlah));
        Assert.assertTrue(allAll.implies(allBaz));
        Assert.assertTrue(allAll.implies(justGetBlah));
        Assert.assertTrue(allAll.implies(justSetBlah));
        Assert.assertTrue(allAll.implies(justGetAndSetBlah));

        Assert.assertTrue(getAll.implies(getAll));
        Assert.assertTrue(getAll.implies(justGetBlah));

        Assert.assertFalse(allBlah.implies(allAll));
        Assert.assertFalse(getAll.implies(allAll));
        Assert.assertFalse(getAll.implies(allBaz));
        Assert.assertFalse(allBlah.implies(getAll));
    }

    @Test
    public void testCollection() {
        final PermissionCollection collection = new ContextPermission("*", "*").newPermissionCollection();
        Assert.assertFalse(collection.implies(new ContextPermission("blah", "get")));
        collection.add(new ContextPermission("blah", "get"));
        Assert.assertTrue(collection.implies(new ContextPermission("blah", "get")));
        Assert.assertFalse(collection.implies(new ContextPermission("blah", "*")));
        Assert.assertFalse(collection.implies(new ContextPermission("*", "get")));
        collection.add(new ContextPermission("blah", "getGlobalDefault"));
        Assert.assertTrue(collection.implies(new ContextPermission("blah", "get")));
        Assert.assertTrue(collection.implies(new ContextPermission("blah", "getGlobalDefault")));
        Assert.assertFalse(collection.implies(new ContextPermission("blah", "*")));
        Assert.assertFalse(collection.implies(new ContextPermission("*", "get")));
        Enumeration<Permission> elements;
        elements = collection.elements();
        Assert.assertTrue(elements.hasMoreElements());
        Assert.assertEquals(new ContextPermission("blah", "get,getGlobalDefault"), elements.nextElement());
        Assert.assertFalse(elements.hasMoreElements());
        collection.add(new ContextPermission("*", "get"));
        elements = collection.elements();
        Assert.assertTrue(elements.hasMoreElements());
        Assert.assertEquals(new ContextPermission("*", "get"), elements.nextElement());
        Assert.assertTrue(elements.hasMoreElements());
        Assert.assertEquals(new ContextPermission("blah", "getGlobalDefault"), elements.nextElement());
        Assert.assertFalse(elements.hasMoreElements());
        collection.add(new ContextPermission("*", "*"));
        elements = collection.elements();
        Assert.assertTrue(elements.hasMoreElements());
        Assert.assertEquals(new ContextPermission("*", "*"), elements.nextElement());
        Assert.assertFalse(elements.hasMoreElements());
    }
}
