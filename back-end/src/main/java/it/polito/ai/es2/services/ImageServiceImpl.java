package it.polito.ai.es2.services;

import it.polito.ai.es2.dtos.ImageDTO;
import it.polito.ai.es2.entities.Image;
import it.polito.ai.es2.repositories.*;
import it.polito.ai.es2.services.exceptions.ImageException;
import it.polito.ai.es2.services.exceptions.ImageNotFoundException;
import it.polito.ai.es2.services.interfaces.ImageService;
import it.polito.ai.es2.services.interfaces.NotificationService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Descrizione classe<p>Politica di sovrascrittura adottata: in quasi tutti i metodi add, se un id era già presente nel database non sovrascrivo i dati
 * già esistenti (tranne nel caso di proposeTeam, che poichè ha un id autogenerato, si è deciso di aggiornare il team vecchio usando
 * sempre la proposeTeam).
 */
@Service
@Transactional
@Log
@Validated
@PreAuthorize("permitAll()")
public class ImageServiceImpl extends CommonURL implements ImageService {
  @Autowired
  ModelMapper modelMapper;
  @Autowired
  CourseRepository courseRepository;
  @Autowired
  StudentRepository studentRepository;
  @Autowired
  TeamRepository teamRepository;
  @Autowired
  NotificationService notificationService;
  @Autowired
  AssignmentRepository assignmentRepository;
  @Autowired
  ImageRepository imageRepository;
  @Autowired
  ImplementationRepository implementationRepository;
  @Autowired
  VMRepository vmRepository;

  /**
   * POST {@link it.polito.ai.es2.controllers.APIImages_RestController#uploadImage(MultipartFile)}
   */
  @Override
  public ImageDTO uploadImage(@NotNull MultipartFile file) {
    if (file == null)
      throw new ImageException("null parameter");
    if (file.isEmpty())
      throw new ImageException("empty file");
    Image img = new Image();
    img.setName(file.getOriginalFilename());
    img.setType(file.getContentType());
    try {
      img.setPicBytes(compressBytes(file.getBytes()));
    } catch (IOException e) {
      e.printStackTrace();
      throw new ImageException("IOException file");
    }
    Image savedImage = imageRepository.save(img);
    img.setDirectLink(baseUrl + "/api/images/direct/" + savedImage.getId());
    imageRepository.flush(); // NECESSARY! Otherwise auto generated fields will remain null (not the id, the auto generated timestamps)
    ImageDTO map = modelMapper.map(savedImage, ImageDTO.class);
    return map;
  }

  /**
   * GET {@link it.polito.ai.es2.controllers.APIImages_RestController#getImage(Long)}
   */
  @Override
  public ImageDTO getImage(@NotNull Long imageId) {
    if (imageId == null)
      throw new ImageException("null id");
    Optional<Image> imageOptional = imageRepository.findById(imageId);
    if (imageOptional.isEmpty())
      throw new ImageNotFoundException(imageId.toString());
    Image retrievedImage = imageOptional.get();
    ImageDTO imageDTO = modelMapper.map(retrievedImage, ImageDTO.class);
    byte[] bytes = decompressBytes(retrievedImage.getPicBytes());
    imageDTO.setImageStringBase64(Base64.getEncoder().encodeToString(bytes));
    return imageDTO;
  }

  @Override public byte[] getBytesImage(@NotNull Long imageId) {
    Optional<Image> imageOptional = imageRepository.findById(imageId);
    if (imageOptional.isEmpty())
      return new byte[0];
    byte[] picBytes = imageOptional.get().getPicBytes();
    return decompressBytes(picBytes);
  }

  // Compress the image bytes before storing it in the database
  private byte[] compressBytes(byte[] data) {
    log.info("Original Image Byte Size - " + data.length);
    Deflater deflater = new Deflater();
    deflater.setInput(data);
    deflater.finish();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] buffer = new byte[1024];
    while (!deflater.finished()) {
      int count = deflater.deflate(buffer);
      outputStream.write(buffer, 0, count);
    }
    try {
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
      throw new ImageException("IOException");
    }
    log.info("Compressed Image Byte Size - " + outputStream.toByteArray().length);
    return outputStream.toByteArray();
  }

  // Uncompress the image bytes before returning it to the angular application
  private byte[] decompressBytes(byte[] data) {
    Inflater inflater = new Inflater();
    inflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] buffer = new byte[1024];
    try {
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        outputStream.write(buffer, 0, count);
      }
      outputStream.close();
    } catch (IOException | DataFormatException e) {
      e.printStackTrace();
      throw new ImageException("IOException | DataFormatException");
    }
    byte[] decompressed = outputStream.toByteArray();
    log.info("Original: " + data.length + " bytes. " + "Decompressed: " + decompressed.length + " bytes.");
    return decompressed;
  }
}
