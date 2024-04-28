import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterLink} from "@angular/router";

import { AuthService } from '@auth0/auth0-angular';
import {PieChartComponent} from "../../components/charts/pie-chart/pie-chart.component";
import {LineChartComponent} from "../../components/charts/line-chart/line-chart.component";
import {DashboardService} from "../../services/dashboard.service";
import {ColumnChartComponent} from "../../components/charts/column-chart/column-chart.component";
import {NgApexchartsModule} from "ng-apexcharts";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, NgApexchartsModule, PieChartComponent, ColumnChartComponent, LineChartComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit{
  userId: string = null;

  // Response from backend
  protected shoppingPattern = null;
  protected tagExpensesDashboard = null;
  protected monthlyExpenses = null;
  protected chainExpenses = null;

  // Data for charts
  protected pieChartData: any;
  protected columnChartDataTags: any;
  protected columnChartDataMonthly: any;

  showPopUp: boolean = true;
  constructor(public auth: AuthService, private dashboardService: DashboardService) {}

  getShoppingPattern(): void {
    this.dashboardService.getShoppingPattern(this.userId).subscribe(
      data => {
        this.shoppingPattern = data;
        console.log("Shopping pattern: ", this.shoppingPattern);
      },
      error => console.error("Error fetching chains: ", error)
    );
  }

  getTagExpenses(): void {
    this.dashboardService.getTagExpenses(this.userId).subscribe(
      data => {
        this.tagExpensesDashboard = data;
        console.log("Tag expenses: ", this.tagExpensesDashboard);
        this.generateTagExpensesChart()
      },
      error => console.error("Error fetching categories: ", error)
    );
  }

  getMonthlyExpenses(): void {
    this.dashboardService.getMonthlyExpenses(this.userId).subscribe(
      data => {
        this.monthlyExpenses = data;
        console.log("Monthly expenses: ", this.monthlyExpenses);
        this.generateMonthlyExpensesChart()
      },
      error => console.error("Error fetching monthly expenses: ", error)
    );
  }

   getChainExpenses(): void {
     this.dashboardService.getChainExpenses(this.userId).subscribe(
       data => {
         this.chainExpenses = data;
         console.log("Chain expenses: ", this.chainExpenses);
         this.generateChainExpensesChart()
       },
       error => console.error("Error fetching categories: ", error)
     );
   }

   generateTagExpensesChart(): void {
     // filter chains to be ignored (e.g. Chain with id 20)
     const filteredTagExpenses = this.tagExpensesDashboard.filter(item => item.categoryName !== "Unkategorisiert" && item.categoryName !== "Nicht auslesbar");

     // Extract data for charts from filtered chains
     const series = filteredTagExpenses.map(item => item.totalExpense);
     const xAxis = filteredTagExpenses.map(item => item.categoryName);

     this.columnChartDataTags = {
       series: [
         {
           data: series,
         },
       ],
       chart: {
         type: 'bar',
         height: 400,
       },
       xaxis: {
         categories: xAxis,
       },
       plotOptions: {
         bar: {
           distributed: true
         }
       },
     };
   }

  generateMonthlyExpensesChart(): void {
    // filter chains to be ignored (e.g. Chain with id 20)
    const filteredMonthlyExpenses = this.monthlyExpenses.filter(item => item.month !== "2024-01");

    // Extract data for charts from filtered chains
    const series = filteredMonthlyExpenses.map(item => item.totalAmount);
    const xAxis = filteredMonthlyExpenses.map(item => item.month);

    this.columnChartDataMonthly = {
      series: [
        {
          data: series,
        },
      ],
      chart: {
        type: 'bar',
        height: 300,
      },
      xaxis: {
        categories: xAxis,
      },
      plotOptions: {
        bar: {
          distributed: true
        }
      },
    };
  }

  generateChainExpensesChart(): void {
    // filter chains to be ignored (e.g. Chain with id 20)
    const filteredChainExpenses = this.chainExpenses.filter(item => item.chainName !== "Feinkost Kolinski" && item.chainName !== "UKAUF HE INSTADT GR ABHEPPACH");

    // Extract data for charts from filtered chains
    const series = filteredChainExpenses.map(item => item.totalExpense);
    const labels = filteredChainExpenses.map(item => item.chainName);

    this.pieChartData = {
      options: {
        series: series,
        labels: labels
      },
      chart: {
        type: 'pie',
        height: 300,
      },
    };
  }

  ngOnInit() {
    // Get user ID
    this.auth.user$.subscribe(
      (profile) => {
        this.userId = encodeURIComponent(profile.sub);

        this.getShoppingPattern()
        this.getTagExpenses()
        this.getMonthlyExpenses()
        this.getChainExpenses()
      }
    );
  }


}
