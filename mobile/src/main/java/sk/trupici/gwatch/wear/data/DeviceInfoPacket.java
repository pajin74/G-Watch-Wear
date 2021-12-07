/*
 * Copyright (C) 2019 Juraj Antal
 *
 * Originally created in G-Watch App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.trupici.gwatch.wear.data;

import java.util.List;

public class DeviceInfoPacket extends TLVPacket {

    public static final byte TAG_BATTERY_STATUS = 0x01;

    public DeviceInfoPacket(List<TLV> tlvList, int totalLen) {
        super(tlvList, totalLen, PacketType.DEVICE_INFO);
    }

    @Override
    protected String getPacketName() {
        return "DEVICE INFO";
    }
}
