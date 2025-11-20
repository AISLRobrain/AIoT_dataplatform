package aisl.ksensor.servicemanager.parameterbroker.service;

import aisl.ksensor.servicemanager.parameterbroker.data.dto.DefaultBrokerMassageDTO;

public interface ManagementBrokerService {

    <T> boolean serviceSetup(String serviceId, T content);

    <T> boolean engineRun(String serviceId, Integer optIter, String engineType, T content);
}
