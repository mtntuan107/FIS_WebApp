import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PostService} from '../services/post.service';

@Component({
  selector: 'app-posts',
  imports: [CommonModule],
  templateUrl: 'post.component.html',
  styleUrls: ['post.component.css']
})
export class PostComponent implements OnInit{
  posts: any[] = [];
  loading: any;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.postService.getPosts().subscribe({
      next: (data) => {
        this.posts = data;
      },
      error: (err) => {
        console.error('Error fetching posts:', err);
      },
    });
  }
}
