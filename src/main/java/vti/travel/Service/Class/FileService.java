package vti.travel.Service.Class;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vti.travel.Model.Entity.FileEntity;
import vti.travel.Repository.FileRepository;
import vti.travel.Service.IFileService;
import vti.travel.utils.FileManager;

import java.io.IOException;
import java.util.Date;

@Service
public class FileService implements IFileService {
    @Autowired
    FileRepository fileRepository;

    private FileManager fileManager = new FileManager();
    private String linkFolder = "A:\\MockTravelProject\\TravelProjectFS\\BackEnd\\Avatar";

    @Override
    public String uploadImage(MultipartFile image) throws IOException {

        String nameImage = new Date().getTime() + "." + fileManager.getFormatFile(image.getOriginalFilename());

        String path = linkFolder + "\\" + nameImage;

        fileManager.createNewMultiPartFile(path, image);

        // TODO save link file to database

        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(nameImage);
        fileEntity.setPath(path);
        fileRepository.save(fileEntity);
        // return link uploaded file
        return nameImage;
    }
}
