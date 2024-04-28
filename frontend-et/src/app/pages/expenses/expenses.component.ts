import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LineChartComponent} from "../../components/charts/line-chart/line-chart.component";
import {FilterService} from "../../services/filter.service";
import {AuthService} from "@auth0/auth0-angular";
import {ExpensesService} from "../../services/expenses.service";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-expenses',
  standalone: true,
  imports: [CommonModule, LineChartComponent, FormsModule],
  templateUrl: './expenses.component.html',
  styleUrl: './expenses.component.css'
})
export class ExpensesComponent implements OnInit {
  userId: string = null;
  productId: number = null;
  productName: string = null;
  invoiceId: number = null;
  productExpenses = null;
  invoiceItems = null;
  invoices = null;

  filterCategory = null; //categories of user for filter list
  filterChain = null; //chains of user for filter list
  selectedCategory: string | undefined;
  selectedChain: string | undefined;

  showArticle: boolean = false;
  reverseOrder: boolean = false; //false -> latest to oldest, true -> oldest to latest

  constructor(protected auth: AuthService, private expensesService: ExpensesService, private filterService: FilterService) {
  }

  ngOnInit(): void {
    // Get user ID
    this.auth.user$.subscribe(
      (profile) => {
        this.userId = encodeURIComponent(profile.sub);

        this.getChains()
        this.getCategories()
        this.getInvoices()
      }
    );
  }

  getChains(): void {
    this.filterService.getChains(this.userId).subscribe(
      data => {
        this.filterChain = data;
        console.log("Chains: ", this.filterChain);
      },
      error => console.error("Error fetching chains: ", error)
    );
  }

  getCategories(): void {
    this.filterService.getCategories(this.userId).subscribe(
      data => {
        this.filterCategory = data;
        console.log("Categories: ", this.filterCategory);
      },
      error => console.error("Error fetching categories: ", error)
    );
  }

  getInvoices(): void {
    this.expensesService.getInvoices(this.userId, this.selectedChain, this.selectedCategory).subscribe(
      data => {
        this.invoices = data;
        console.log("Invoices: ", this.invoices);
      },
      error => console.error("Error fetching invoices: ", error)
    );
  }

  getInvoiceItems(id: number): void {
    this.expensesService.getInvoiceItems(this.userId, id).subscribe(
      data => {
        this.invoiceItems = data;
        console.log("Invoice items: ", this.invoiceItems);
      },
      error => console.error("Error fetching invoice items: ", error)
    );
  }

  getProductExpenses(id: number): void {
    this.expensesService.getProductExpenses(this.userId, id).subscribe(
      data => {
        this.productExpenses = data;
        console.log("Product expenses: ", this.productExpenses);
      },
      error => console.error("Error fetching product expenses: ", error)
    );
  }



  toggleInvoice(invoiceId: number): void {
    this.invoiceId = invoiceId;
    console.log(this.invoiceId)
    this.getInvoiceItems(invoiceId)
  }

  toggleArticle(productId: number, productName: string): void {
    this.productId = productId;
    this.productName = productName
    console.log(this.productId)
    this.getProductExpenses(productId);
    this.showArticle = true;
  }

  /**
   * Changes order of receipts according to user input.
   * @param reverseOrder - order of receipts in receipts-array
   */
  sortReceipts(reverseOrder: boolean): void {
    this.reverseOrder = reverseOrder;
    this.invoices = this.reverseOrder ? this.invoices.reverse() : this.invoices;
  }

  filterInvoices(): void {
    this.getInvoices();
  }
  resetFilters(): void {
    this.selectedChain = '';
    this.selectedCategory = '';
    this.getInvoices();
  }
}
