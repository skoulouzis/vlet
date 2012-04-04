/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: SRMFileOptions.java,v 1.4 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm;

import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.TFileStorageType;
import gov.lbl.srm.v22.stubs.TOverwriteMode;
import gov.lbl.srm.v22.stubs.TRetentionPolicyInfo;
import gov.lbl.srm.v22.stubs.TTransferParameters;

/**
 * Container class to keep srm file options
 * 
 * @author S. Koulouzis
 * 
 */
public class SRMFileOptions
{

    private Integer desiredFileLifeTime = null;

    private TFileStorageType desiredFileStorageType = TFileStorageType.PERMANENT;

    private Integer desiredPinLifeTime = null;

    private Integer desiredTotalRequestTime = null;

    private TOverwriteMode overwriteOption = TOverwriteMode.ALWAYS;

    private ArrayOfTExtraInfo storageSystemInfo = null;

    private TRetentionPolicyInfo targetFileRetentionPolicyInfo = null;

    private String targetSpaceToken = null;

    private TTransferParameters transferParameters = null;

    /**
     * 
     * Optional input parameter desiredFileLifetime is the lifetime of the SURL
     * when the file is put into the storage system. It does not refer to the
     * lifetime (expiration time) of the TURL. Lifetime on SURL starts when
     * successful srmPutDone is executed.
     * 
     * @param desiredFileLifeTime
     */
    public void setDesiredFileLifeTime(Integer desiredFileLifeTime)
    {
        this.desiredFileLifeTime = desiredFileLifeTime;
    }

    /**
     * Optional input parameter desiredFileLifetime is the lifetime of the SURL
     * when the file is put into the storage system. It does not refer to the
     * lifetime (expiration time) of the TURL. Lifetime on SURL starts when
     * successful srmPutDone is executed.
     * 
     * @return
     */
    public Integer getDesiredFileLifeTime()
    {
        return desiredFileLifeTime;
    }

    /**
     * Volatile file has a lifetime and the storage may delete all traces of the
     * file when it expires.
     * 
     * Permanent file has no expiration time.
     * 
     * Durable file has an expiration time, but the storage may not delete the
     * file, and should raise error condition instead.
     * 
     * @param desiredFileStorageType
     */
    public void setDesiredFileStorageType(TFileStorageType desiredFileStorageType)
    {
        this.desiredFileStorageType = desiredFileStorageType;
    }

    /**
     * Volatile file has a lifetime and the storage may delete all traces of the
     * file when it expires.
     * 
     * Permanent file has no expiration time.
     * 
     * Durable file has an expiration time, but the storage may not delete the
     * file, and should raise error condition instead.
     * 
     * @return
     */
    public TFileStorageType getDesiredFileStorageType()
    {
        return desiredFileStorageType;
    }

    /**
     * Optional input parameter desiredPinLifetime is the lifetime (expiration
     * time) on the TURL when the Transfer URL is prepared. It does not refer to
     * the lifetime of the SURL. TURLs must not be valid any more after the
     * desiredPinLifetime is over if srmPutDone or srmAbortRequest is not
     * submitted on the SURL before expiration. In such case, the SRM must
     * return SRM_FAILURE at the file level.
     * 
     * @param desiredPinLifeTime
     */
    public void setDesiredPinLifeTime(Integer desiredPinLifeTime)
    {
        this.desiredPinLifeTime = desiredPinLifeTime;
    }

    /**
     * Optional input parameter desiredPinLifetime is the lifetime (expiration
     * time) on the TURL when the Transfer URL is prepared. It does not refer to
     * the lifetime of the SURL. TURLs must not be valid any more after the
     * desiredPinLifetime is over if srmPutDone or srmAbortRequest is not
     * submitted on the SURL before expiration. In such case, the SRM must
     * return SRM_FAILURE at the file level.
     * 
     * @return
     */
    public Integer getDesiredPinLifeTime()
    {
        return desiredPinLifeTime;
    }

    /**
     * If input parameter desiredTotalRequestTime is unspecified as NULL, the
     * request must be retried for a duration which is dependent on the SRM.
     * 
     * If input parameter desiredTotalRequestTime is 0 (zero), each file request
     * must be tried at least once. Negative value must be invalid.
     * 
     * @param desiredTotalRequestTime
     */
    public void setDesiredTotalRequestTime(Integer desiredTotalRequestTime)
    {
        this.desiredTotalRequestTime = desiredTotalRequestTime;
    }

    /**
     * If input parameter desiredTotalRequestTime is unspecified as NULL, the
     * request must be retried for a duration which is dependent on the SRM.
     * 
     * If input parameter desiredTotalRequestTime is 0 (zero), each file request
     * must be tried at least once. Negative value must be invalid.
     * 
     * @return
     */
    public Integer getDesiredTotalRequestTime()
    {
        return desiredTotalRequestTime;
    }

    /**
     * Use case for WHEN_FILES_ARE_DIFFERENT can be that files are different
     * when the declared size for an SURL is different from the actual one, or
     * that the checksum of an SURL is different from the actual one.
     * 
     * Use case for WHEN_FILES_ARE_DIFFERENT can be that files are different
     * when the declared size for an SURL is different from the actual one, or
     * that the checksum of an SURL is different from the actual one.
     * 
     * o Overwrite mode on a file is considered higher priority than pinning a
     * file. Where applicable, it allows to mark a valid Transfer URL to become
     * invalid when the owner of the SURL issues an overwrite request. Overwrite
     * mode on a file is considered higher priority than pinning a file. Where
     * applicable, it allows to mark a valid Transfer URL to become invalid when
     * the owner of the SURL issues an overwrite request.
     * 
     * @param overwriteOption
     */
    public void setOverwriteOption(TOverwriteMode overwriteOption)
    {
        this.overwriteOption = overwriteOption;
    }

    /**
     * Use case for WHEN_FILES_ARE_DIFFERENT can be that files are different
     * when the declared size for an SURL is different from the actual one, or
     * that the checksum of an SURL is different from the actual one.
     * 
     * Use case for WHEN_FILES_ARE_DIFFERENT can be that files are different
     * when the declared size for an SURL is different from the actual one, or
     * that the checksum of an SURL is different from the actual one.
     * 
     * o Overwrite mode on a file is considered higher priority than pinning a
     * file. Where applicable, it allows to mark a valid Transfer URL to become
     * invalid when the owner of the SURL issues an overwrite request. Overwrite
     * mode on a file is considered higher priority than pinning a file. Where
     * applicable, it allows to mark a valid Transfer URL to become invalid when
     * the owner of the SURL issues an overwrite request.
     * 
     * @return
     */
    public TOverwriteMode getOverwriteOption()
    {
        return overwriteOption;
    }

    /**
     * TExtraInfo is used where additional information is needed, such as for
     * additional information for transfer protocols of TURLs in srmPing,
     * srmGetTransferProtocols, srmStatusOfGetRequest, and
     * srmStatusOfPutRequest. For example, when it is used for additional
     * information for transfer protocols, the keys may specify access speed,
     * available number of parallelism, and other transfer protocol properties.
     * 
     * It is also used where additional information to the underlying storage
     * system is needed, such as for storage device, storage login ID, storage
     * login authorization, but not limited to. Formerly, it was
     * TStorageSystemInfo.
     * 
     * @param storageSystemInfo
     */
    public void setStorageSystemInfo(ArrayOfTExtraInfo storageSystemInfo)
    {
        this.storageSystemInfo = storageSystemInfo;
    }

    /**
     * TExtraInfo is used where additional information is needed, such as for
     * additional information for transfer protocols of TURLs in srmPing,
     * srmGetTransferProtocols, srmStatusOfGetRequest, and
     * srmStatusOfPutRequest. For example, when it is used for additional
     * information for transfer protocols, the keys may specify access speed,
     * available number of parallelism, and other transfer protocol properties.
     * 
     * It is also used where additional information to the underlying storage
     * system is needed, such as for storage device, storage login ID, storage
     * login authorization, but not limited to. Formerly, it was
     * TStorageSystemInfo.
     * 
     * @return
     */
    public ArrayOfTExtraInfo getStorageSystemInfo()
    {
        return storageSystemInfo;
    }

    /**
     * Quality of Retention (Storage Class) is a kind of Quality of Service. It
     * refers to the probability that the storage system lose a file. Numeric
     * probabilities are self-assigned.
     * 
     * Replica quality has the highest probability of loss, but is appropriate
     * for data that can be replaced because other copies can be accessed in a
     * timely fashion.
     * 
     * Output quality is an intermediate level and refers to the data which can
     * be replaced by lengthy or effort-full processes.
     * 
     * Custodial quality provides low probability of loss.
     * 
     * 
     * These terms are used to describe how latency to access a file is
     * improvable. Latency is improved by storage systems replicating a file
     * such that its access latency is online.
     * 
     * · The ONLINE cache of a storage system is the part of the storage system
     * which provides file with online latencies.
     * 
     * · ONLINE has the lowest latency possible. No further latency improvements
     * are applied to online files.
     * 
     * · NEARLINE file can have their latency improved to online latency
     * automatically by staging the file to online cache.
     * 
     * · For completeness, we also describe OFFLINE here.
     * 
     * · OFFLINE files need a human to be involved to achieve online latency.
     * 
     * The type will be used to describe a space property that access latency
     * can be requested at the time of space reservation. The content of the
     * space, files may have the same or “lesser” access latency as the space.
     * 
     * For the SRM, ONLINE and NEARLINE are specified, and files may be ONLINE
     * and/or NEARLINE.
     * 
     * @param targetFileRetentionPolicyInfo
     */
    public void setTargetFileRetentionPolicyInfo(TRetentionPolicyInfo targetFileRetentionPolicyInfo)
    {
        this.targetFileRetentionPolicyInfo = targetFileRetentionPolicyInfo;
    }

    /**
     * Quality of Retention (Storage Class) is a kind of Quality of Service. It
     * refers to the probability that the storage system lose a file. Numeric
     * probabilities are self-assigned.
     * 
     * Replica quality has the highest probability of loss, but is appropriate
     * for data that can be replaced because other copies can be accessed in a
     * timely fashion.
     * 
     * Output quality is an intermediate level and refers to the data which can
     * be replaced by lengthy or effort-full processes.
     * 
     * Custodial quality provides low probability of loss.
     * 
     * 
     * These terms are used to describe how latency to access a file is
     * improvable. Latency is improved by storage systems replicating a file
     * such that its access latency is online.
     * 
     * · The ONLINE cache of a storage system is the part of the storage system
     * which provides file with online latencies.
     * 
     * · ONLINE has the lowest latency possible. No further latency improvements
     * are applied to online files.
     * 
     * · NEARLINE file can have their latency improved to online latency
     * automatically by staging the file to online cache.
     * 
     * · For completeness, we also describe OFFLINE here.
     * 
     * · OFFLINE files need a human to be involved to achieve online latency.
     * 
     * The type will be used to describe a space property that access latency
     * can be requested at the time of space reservation. The content of the
     * space, files may have the same or “lesser” access latency as the space.
     * 
     * For the SRM, ONLINE and NEARLINE are specified, and files may be ONLINE
     * and/or NEARLINE.
     * 
     * @return
     */
    public TRetentionPolicyInfo getTargetFileRetentionPolicyInfo()
    {
        return targetFileRetentionPolicyInfo;
    }

    public void setTargetSpaceToken(String targetSpaceToken)
    {
        this.targetSpaceToken = targetSpaceToken;
    }

    public String getTargetSpaceToken()
    {
        return targetSpaceToken;
    }

    public void setTransferParameters(TTransferParameters transferParameters)
    {
        this.transferParameters = transferParameters;
    }

    public TTransferParameters getTransferParameters()
    {
        return transferParameters;
    }

}
