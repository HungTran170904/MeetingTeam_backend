package com.HungTran.MeetingTeam.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.HungTran.MeetingTeam.Exception.FileException;
import com.HungTran.MeetingTeam.Util.InfoChecking;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
	private final Cloudinary cloudinary;
	private final InfoChecking infoChecking;
	private Random rand = new Random();

	public String uploadFile(MultipartFile file,String folder,String url) {
		try {
			var filename=file.getOriginalFilename();
			String format=filename.substring(filename.lastIndexOf(".")+1);

			Map<String, Object> options=ObjectUtils.asMap(
				    "resource_type","auto",
				    "type","authenticated",
				    "overwrite","true",
				    "invalidate","true",
				    "folder",folder,
				    "format",format,
					"public_id", filename+"_"+rand.nextInt(10000)
					);
			if(url!=null) {
				String[] strs=url.split("/");
				System.out.println("Last string plit [.]: "+strs[strs.length-1].split("[.]"));
				String publicId=strs[strs.length-1].split("[.]")[0];
				options.put("public_id",publicId);
			}
			Map<String, Object> result= cloudinary.uploader().upload(file.getBytes(),options);
			return result.get("secure_url").toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileException("Could not save the uploaded file!Please try again!");
		}
	}
	public void deleteFile(String url) {
		String[] strs=url.split("/");
		String publicId=strs[strs.length-1].split(".")[0];
		try {
			cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
		} catch (IOException e) {
			throw new FileException("Sorry! Could not delete the resource");
		}
	}
}
