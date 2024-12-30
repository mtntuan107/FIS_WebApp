import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DessertCreateComponent } from './dessert-create.component';

describe('DessertCreateComponent', () => {
  let component: DessertCreateComponent;
  let fixture: ComponentFixture<DessertCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DessertCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DessertCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
