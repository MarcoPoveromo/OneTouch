package android.stage.onetouch.model;

import java.util.Objects;

public class CpeInfo {
    private String vendorName;
    private String model;
    private String softwareVersion;
    private String osFamily;
    private String upTime;
    private String lastReboot;
    private String processor;
    private String configurationMemory; // [bytes]
    private String flashSize; // [bytes]
    private String hostName;
    private String deviceSerialNumber;
    private String deviceName;


    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getOsFamily() {
        return osFamily;
    }

    public void setOsFamily(String osFamily) {
        this.osFamily = osFamily;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getLastReboot() {
        return lastReboot;
    }

    public void setLastReboot(String lastReboot) {
        this.lastReboot = lastReboot;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getConfigurationMemory() {
        return configurationMemory;
    }

    public void setConfigurationMemory(String configurationMemory) {
        this.configurationMemory = configurationMemory;
    }

    public String getFlashSize() {
        return flashSize;
    }

    public void setFlashSize(String flashSize) {
        this.flashSize = flashSize;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CpeInfo)) return false;
        CpeInfo cpeInfo = (CpeInfo) o;
        return Objects.equals(getVendorName(), cpeInfo.getVendorName()) &&
                Objects.equals(getModel(), cpeInfo.getModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVendorName(), getModel());
    }

    @Override
    public String toString() {
        return "CpeInfo{" +
                "vendorName='" + vendorName + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}

