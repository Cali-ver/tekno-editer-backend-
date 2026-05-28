package com.canvaclone.service;

import com.canvaclone.dto.DesignDto;
import com.canvaclone.entity.Design;
import com.canvaclone.exception.ResourceNotFoundException;
import com.canvaclone.exception.UnauthorizedException;
import com.canvaclone.repository.DesignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DesignService {

    @Autowired
    private DesignRepository designRepository;

    public DesignDto.DesignResponse createDesign(String email, DesignDto.CreateRequest request) {
        Design design = Design.builder()
                .title(request.getTitle())
                .width(request.getWidth())
                .height(request.getHeight())
                .jsonData(request.getJsonData())
                .thumbnail(request.getThumbnail())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .ownerEmail(email)
                .build();

        Design savedDesign = designRepository.save(design);
        return mapToResponse(savedDesign);
    }

    public List<DesignDto.DesignResponse> getAllDesigns(String email) {
        return designRepository.findAllByOwnerEmailOrderByUpdatedAtDesc(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DesignDto.DesignResponse> getPublicDesigns() {
        return designRepository.findAllByIsPublicTrueOrderByUpdatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DesignDto.DesignResponse getDesignById(String email, Long id) {
        Design design = designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found"));
        
        boolean isOwner = email != null && design.getOwnerEmail().equals(email);
        boolean isShared = !"ONLY_YOU".equals(design.getAccessLevel());
        boolean isPublic = design.isPublic();

        if (!isOwner && !isShared && !isPublic) {
            throw new UnauthorizedException("You do not have access to this design");
        }
        
        return mapToResponse(design);
    }

    public DesignDto.DesignResponse updateDesign(String email, Long id, DesignDto.UpdateRequest request) {
        Design design = designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found"));

        if (!design.getOwnerEmail().equals(email)) {
            throw new UnauthorizedException("You do not have access to this design");
        }

        if (request.getTitle() != null) design.setTitle(request.getTitle());
        if (request.getJsonData() != null) design.setJsonData(request.getJsonData());
        if (request.getThumbnail() != null) design.setThumbnail(request.getThumbnail());
        if (request.getAccessLevel() != null) design.setAccessLevel(request.getAccessLevel());
        if (request.getIsPublic() != null) design.setPublic(request.getIsPublic());

        Design updatedDesign = designRepository.save(design);
        return mapToResponse(updatedDesign);
    }

    public void deleteDesign(String email, Long id) {
        Design design = designRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Design not found"));

        if (!design.getOwnerEmail().equals(email)) {
            throw new UnauthorizedException("You do not have access to this design");
        }

        designRepository.delete(design);
    }

    private DesignDto.DesignResponse mapToResponse(Design design) {
        return DesignDto.DesignResponse.builder()
                .id(design.getId())
                .title(design.getTitle())
                .width(design.getWidth())
                .height(design.getHeight())
                .thumbnail(design.getThumbnail())
                .jsonData(design.getJsonData())
                .accessLevel(design.getAccessLevel())
                .ownerEmail(design.getOwnerEmail())
                .isPublic(design.isPublic())
                .createdAt(design.getCreatedAt())
                .updatedAt(design.getUpdatedAt())
                .build();
    }
}
