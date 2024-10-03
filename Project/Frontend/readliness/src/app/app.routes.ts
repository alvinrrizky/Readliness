import { Routes } from '@angular/router';
import { DisplayOrderComponent } from './pages/order/display-order/display-order.component';
import { AddOrderComponent } from './pages/order/add-order/add-order.component';
import { EditOrderComponent } from './pages/order/edit-order/edit-order.component';
import { DetailOrderComponent } from './pages/order/detail-order/detail-order.component';
import { DisplayCustomerComponent } from './pages/customer/display-customer/display-customer.component';
import { AddCustomerComponent } from './pages/customer/add-customer/add-customer.component';
import { EditCustomerComponent } from './pages/customer/edit-customer/edit-customer.component';
import { DetailCustomerComponent } from './pages/customer/detail-customer/detail-customer.component';
import { DisplayItemComponent } from './pages/item/display-item/display-item.component';
import { AddItemComponent } from './pages/item/add-item/add-item.component';
import { EditItemComponent } from './pages/item/edit-item/edit-item.component';
import { DetailItemComponent } from './pages/item/detail-item/detail-item.component';
import { LayoutComponent } from './pages/layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'order/display',
        pathMatch: 'full',
      },
      {
        path: 'customer',
        children: [
          { path: 'display', component: DisplayCustomerComponent },
          { path: 'add', component: AddCustomerComponent },
          { path: 'edit/:id', component: EditCustomerComponent },
          { path: 'detail/:id', component: DetailCustomerComponent },
        ],
      },
      {
        path: 'order',
        children: [
          { path: 'display', component: DisplayOrderComponent },
          { path: 'add', component: AddOrderComponent },
          { path: 'edit/:id', component: EditOrderComponent },
          { path: 'detail/:id', component: DetailOrderComponent },
        ],
      },
      {
        path: 'item',
        children: [
          { path: 'display', component: DisplayItemComponent },
          { path: 'add', component: AddItemComponent },
          { path: 'edit/:id', component: EditItemComponent },
          { path: 'detail/:id', component: DetailItemComponent },
        ],
      },
    ],
  },
];
