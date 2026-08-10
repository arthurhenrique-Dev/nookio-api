package com.henrique.nookio_api.modules.files.repository;

import com.henrique.nookio_api.modules.files.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Integer> {
}
