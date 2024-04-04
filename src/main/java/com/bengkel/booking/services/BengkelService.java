package com.bengkel.booking.services;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class BengkelService {
	static DecimalFormat currencyFormatter = new DecimalFormat("Rp#,##0");

	private static double saldoKoin = 0;
	private static boolean isMember = false;
	private static int loginCount = 0;

	// Silahkan tambahkan fitur-fitur utama aplikasi disini

	// Login
	public static String loginService(String customerID, String customerPassword, List<Customer> listAllCustomers) {
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
				return customer.getCustomerId();
			} else {
				System.out.println("Alert: Maaf Password Yang Anda Masukkan Salah!");
				loginCount++;
				return null;
			}
		} else {
			System.out.println("Alert: Maaf, Customer Tidak Ditemukan atau Salah!");
			loginCount++;
			return null;
		}
	}

	// Info Customer
	public static Boolean getCustomerInformationService(String customerID, List<Customer> listAllCustomers) {
		Optional<Customer> customer = listAllCustomers.stream()
				.filter(c -> c.getCustomerId().equals(customerID))
				.findFirst();
		if (customer.isPresent()) {
			if (customer.get() instanceof MemberCustomer) {
				MemberCustomer memberCustomer = (MemberCustomer) customer.get();

				isMember = true;
				saldoKoin = memberCustomer.getSaldoCoin();
			}

			System.out.println("Data Informasi Customer:");
			System.out.println("Customer ID: " + customer.get().getCustomerId());
			System.out.println("Nama: " + customer.get().getName());
			System.out.println("Customer Status: " + (isMember ? "Member" : "none"));
			System.out.println("Alamat: " + customer.get().getAddress());
			System.out.println("Saldo Koin: " + currencyFormatter.format(saldoKoin));
			System.out.println("List Kendaraan: ");
			for (Vehicle vehicle : customer.get().getVehicles()) {
				System.out.println(" - " + vehicle.getBrand());
			}
		} else {
			System.out.println("Gagal menampilkan informasi");
		}
		return true;
	}

	// Booking atau Reservation

	// Top Up Saldo Coin Untuk Member Customer
	public static void topupSaldoService(String customerID, List<Customer> listAllCustomers, Scanner input) {
		Optional<Customer> customer = listAllCustomers.stream()
				.filter(c -> c.getCustomerId().equals(customerID))
				.findFirst();
		if (customer.isPresent()) {
			if (customer.get() instanceof MemberCustomer) {
				System.out.print("Jumlah topup saldo: ");
				int jumlahTopup = input.nextInt();

				MemberCustomer memberCustomer = (MemberCustomer) customer.get();

				if (memberCustomer.getCustomerId() == customerID) {
					double updatedSaldo = memberCustomer.topupSaldo(jumlahTopup);

					memberCustomer.setSaldoCoin(updatedSaldo);
					System.out.println("Saldo berhasil topup.");
					System.out.println("Jumlah saldo saat ini: " + currencyFormatter.format(updatedSaldo));
				} else {
					System.out.println("Saldo gagal topup.");
				}
			} else {
				System.out.println("Maaf fitur ini hanya untuk Member saja!");
			}
		}
	}
}
