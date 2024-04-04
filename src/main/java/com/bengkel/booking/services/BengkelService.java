package com.bengkel.booking.services;

import java.util.List;
import java.util.Optional;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.MemberCustomer;

public class BengkelService {
	private static int loginCount = 0;

	// Silahkan tambahkan fitur-fitur utama aplikasi disini

	// Login
	public static boolean loginService(String customerID, String customerPassword, List<Customer> listAllCustomers) {
		if (loginCount >= 3) {
			System.out.println("Alert: Maaf, Anda terlalu banyak percobaan");
			System.exit(0);
		}

		Optional<Customer> checkCustomer = listAllCustomers.stream()
				.filter(customer -> customer.getCustomerId().equals(customerID))
				.findFirst();
		if (checkCustomer.isPresent()) {
			Customer customer = checkCustomer.get();
			if (customer.getPassword().equals(customerPassword)) {
				System.out.println("Login Berhasil");
				return true;
			} else {
				System.out.println("Alert: Maaf Password Yang Anda Masukkan Salah!");
				loginCount++;
				return false;
			}
		} else {
			System.out.println("Alert: Maaf, Customer Tidak Ditemukan atau Salah!");
			loginCount++;
			return false;
		}
	}

	// Info Customer

	// Booking atau Reservation

	// Top Up Saldo Coin Untuk Member Customer

	// Logout

}
