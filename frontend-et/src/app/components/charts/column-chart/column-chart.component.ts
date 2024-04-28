import {Component, Input, OnInit} from '@angular/core';
import {NgApexchartsModule} from "ng-apexcharts";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-column-chart',
  standalone: true,
  imports: [
    CommonModule,
    NgApexchartsModule
  ],
  template: `
    <div id="chart">
      <apx-chart
        [series]="chartData.series"
        [chart]="chartData.chart"
        [xaxis]="chartData.xaxis"
        [plotOptions]="chartData.plotOptions"
      ></apx-chart>
    </div>`,
  styleUrl: './column-chart.component.css'
})
export class ColumnChartComponent implements OnInit{
  @Input() chartData: any = {};

  ngOnInit(): void {
  }
}
