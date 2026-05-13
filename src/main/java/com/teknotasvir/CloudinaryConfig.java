package com.teknotasvir;

import com.cloudinary.Cloudinary;

import java.util.Map;

public class CloudinaryConfig {
    public static Cloudinary cloudinary = new Cloudinary(
            Map.of(
                    "cloud_name", "dspthl1h7",
                    "api_key", "274643336624462",
                    "api_secret", "1yTaxZi05Ayfdlwu0FiuXFYrxW4"
            )
    );
}
