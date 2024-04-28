import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgApexchartsModule} from "ng-apexcharts";

@Component({
  selector: 'app-pie-chart',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  template: `
    <div id="chart">
      <apx-chart
        [series]="chartData.options.series"
        [labels]="chartData.options.labels"
        [chart]="chartData.chart"
      ></apx-chart>
    </div>
  `,
  styleUrl: './pie-chart.component.css'
})
export class PieChartComponent implements OnInit {
  @Input() chartData: any;

  ngOnInit() {
    /*this.chartData = {
      options: {
        series: [44, 55, 13],
        labels: ['Edeka', 'Rewe', 'Lidl']
      },
      chart: {
        type: 'pie',
        height: 350,
      },
    };*/
  }
}
