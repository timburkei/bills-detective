import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {NgApexchartsModule} from "ng-apexcharts";

@Component({
  selector: 'app-stacked-column-chart',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  template: `
    <div id="chart">
      <apx-chart
        [series]="chartData.series"
        [chart]="chartData.chart"
        [xaxis]="chartData.xaxis"
      ></apx-chart>
    </div>
  `,
  styleUrl: './column-chart.component.css'
})
export class ColumnChartComponent implements OnInit {
  @Input() chartData: any = {};

  ngOnInit() {
    /*this.chartData = {
    für jedes i setze name:

      series: [{
        name: 'Trauben',
        data: [44, 55, 41, 67, 22, 43]
      }, {
        name: 'Bananen',
        data: [13, 23, 20, 8, 13, 27]
      }, {
        name: 'Kakis',
        data: [11, 17, 15, 15, 21, 14]
      }, {
        name: 'Sonstige',
        data: [21, 7, 25, 13, 22, 8]
      }],
      xaxis: {
        categories: ['Januar', 'Februar', 'März', 'April',
          'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober',
          'November', 'Dezember'
        ],
      },
      chart: {
        type: 'bar',
        height: 350,
        stacked: true,
      },
    };*/
  }
}

