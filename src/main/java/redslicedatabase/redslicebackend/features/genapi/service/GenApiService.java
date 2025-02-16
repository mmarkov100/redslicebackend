package redslicedatabase.redslicebackend.features.genapi.service;

import redslicedatabase.redslicebackend.features.genapi.dto.inbound.MessageGeneratorResponseGenApiDTO;
import redslicedatabase.redslicebackend.features.genapi.dto.outbound.GenApiRequest;
import redslicedatabase.redslicebackend.features.message.dto.inbound.MessageGenerateDTO;

public interface GenApiService<T extends GenApiRequest> {
    MessageGeneratorResponseGenApiDTO generateResponse(T genApiRequest) throws Exception;
    T genApiRequestConvertDTO(MessageGenerateDTO messageGenerateDTO);
}
