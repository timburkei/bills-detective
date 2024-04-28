import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ColumnChartComponent} from "../../components/charts/stacked-column-chart/column-chart.component";
import {AuthService} from "@auth0/auth0-angular";
import {FilterService} from "../../services/filter.service";
import {ExpensesService} from "../../services/expenses.service";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-create-chart',
  standalone: true,
  imports: [CommonModule, ColumnChartComponent],
  templateUrl: './create-chart.component.html',
  styleUrl: './create-chart.component.css'
})
export class CreateChartComponent implements OnInit {
  userId: string = null;
  categories = null;
  chains = null;
  chainTagExpenses = null;

  // Hier kannst du deine Arrays für Kategorien und Läden einsetzen
  selectedCategoriesIds: string[] = [];
  selectedChainsIds: string[] = [];
  selectedCategoriesNames: string[] = [];
  selectedChainsNames: string[] = [];

  stackedColumnChartData: any;

  // FormGroup für Reactive Forms
  filterForm: FormGroup;

  constructor(private fb: FormBuilder, protected auth: AuthService, private filterService: FilterService, private expensesService: ExpensesService) {}

  getChains(): void {
    this.filterService.getChains(this.userId).subscribe(
      data => {
        this.chains = data;
        console.log("Chains: ", this.chains);
      },
      error => console.error("Error fetching chains: ", error)
    );
  }
  getCategories(): void {
    this.filterService.getCategories(this.userId).subscribe(
      data => {
        this.categories = data;
        console.log("Categories: ", this.categories);
      },
      error => console.error("Error fetching categories: ", error)
    );
  }

  generateChart(): void {
    console.log(this.selectedCategoriesIds, this.selectedChainsIds);

    // Auf das Abschließen des Observables warten
    this.expensesService.getChainTagExpenses(this.userId, this.selectedChainsIds, this.selectedCategoriesIds).subscribe(
      data => {
        this.chainTagExpenses = data;

        let series: any[] = [];

        //Über alle ausgewählten Kategorien iterieren:
        for (let category = 0; category < this.selectedCategoriesNames.length; category++) {
          let seriesData: any[] = [];
          //Über alle ausgewählten Läden iterieren
          for (let chain = 0; chain < this.selectedChainsIds.length; chain++) {
            seriesData.push(this.chainTagExpenses[chain].tagExpenses[category].expense)
          }

          series.push({
            name: this.selectedCategoriesNames[category],
            data: seriesData
          });
        }
        this.stackedColumnChartData = {
          series: series,
          chart: {
            type: 'bar',
            height: 350,
            stacked: true,
          },
          xaxis: {
            categories: this.selectedChainsNames,
          }
        };
      },
      error => console.error("Error fetching chain tag expenses: ", error)
    );
  }


  toggleCategory(categoryId: string, categoryName: string): void {
    const index = this.selectedCategoriesIds.indexOf(categoryId);

    if (index === -1) {
      this.selectedCategoriesIds.push(categoryId);
      this.selectedCategoriesNames.push(categoryName)
    } else {
      this.selectedCategoriesIds.splice(index, 1);
      this.selectedCategoriesNames.splice(index, 1);
    }
  }

  toggleChain(chainId: string, chainName: string): void {
    const index = this.selectedChainsIds.indexOf(chainId);

    if (index === -1) {
      this.selectedChainsIds.push(chainId);
      this.selectedChainsNames.push(chainName)
    } else {
      this.selectedChainsIds.splice(index, 1);
      this.selectedChainsNames.splice(index, 1);
    }
  }


  ngOnInit(): void {
    // Initializing Reactive Form
    this.filterForm = this.fb.group({
      selectedCategories: [[]],
      selectedChains: [[]]
    });

    // Überwache Änderungen an den Formular-Controls
    this.filterForm.valueChanges.subscribe(values => {
      this.selectedChainsIds = values.selectedChains;
      this.selectedCategoriesIds = values.selectedCategories;
    });

    // Get user ID
    this.auth.user$.subscribe(
      (profile) => {
        this.userId = encodeURIComponent(profile.sub);

        this.getChains()
        this.getCategories()
      }
    );
  }

}
