/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
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
 * ====================================================================
 */

package org.jclouds.cloudstack.features;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.options.ListPublicIPAddressesOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code PublicIPAddressClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "PublicIPAddressClientLiveTest")
public class AddressClientLiveTest extends BaseCloudStackClientLiveTest {
   private PublicIPAddress ip = null;

   public void testAssociateDisassociatePublicIPAddress() throws Exception {
      AsyncCreateResponse job = client.getAddressClient().associateIPAddress(
               Iterables.get(client.getNetworkClient().listNetworks(), 0).getZoneId());
      checkState(jobComplete.apply(job.getJobId()), "job %d failed to complete", job.getJobId());
      ip = client.getAsyncJobClient().<PublicIPAddress> getAsyncJob(job.getJobId()).getResult();
      checkIP(ip);
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (ip != null) {
         client.getAddressClient().disassociateIPAddress(ip.getId());
      }
      super.tearDown();
   }

   public void testListPublicIPAddresss() throws Exception {
      Set<PublicIPAddress> response = client.getAddressClient().listPublicIPAddresses();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (PublicIPAddress ip : response) {
         PublicIPAddress newDetails = getOnlyElement(client.getAddressClient().listPublicIPAddresses(
                  ListPublicIPAddressesOptions.Builder.id(ip.getId())));
         assertEquals(ip.getId(), newDetails.getId());
         checkIP(ip);
      }
   }

   protected void checkIP(PublicIPAddress ip) {
      assertEquals(ip.getId(), client.getAddressClient().getPublicIPAddress(ip.getId()).getId());
      assert ip.getId() > 0 : ip;
      assert ip.getAccount() != null : ip;
      assert ip.getDomain() != null : ip;
      assert ip.getDomainId() > 0 : ip;
      assert ip.getState() != null : ip;
      assert ip.getZoneId() > 0 : ip;
      assert ip.getZoneName() != null : ip;

   }
}
