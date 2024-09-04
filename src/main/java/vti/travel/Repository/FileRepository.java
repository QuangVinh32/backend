package vti.travel.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vti.travel.Model.Entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
